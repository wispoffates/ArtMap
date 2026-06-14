// E2E paint bot: joins the Paper server booted by run-integration-test.sh (or
// docker-compose.yml locally), places a real ArtMap easel, mounts a canvas,
// rides it and paints strokes by sending the look/swing packets a painting
// player sends. On dismount ArtMap persists the in-progress map to Art.db,
// which the shell script then verifies.
//
// The in-JVM PaintingIntegrationTest covers everything downstream of packet
// decoding (brushes, renderer, compression, SQLite); the goal here is only to
// prove the real network path: join -> easel -> strokes arrive -> dismount
// persists. So we don't need pixel precision, just strokes that land on the
// canvas and reach the database.
//
// Easel setup uses ArtMap's own `give` command over RCON rather than crafting
// the custom-NBT items by hand over protocol, then the bot drives the physical
// interactions (place, mount, look, swing, sneak) like a player would.

import mineflayer from 'mineflayer';
import { Vec3 } from 'vec3';
import { Rcon } from 'rcon-client';

const HOST = process.env.MC_HOST ?? 'localhost';
// Must match the MC_VERSION the run script boots the server with (default
// 1.21.11); the script passes it through as MC_VERSION, this fallback only
// applies when the bot is run standalone against a manually started server.
const VERSION = process.env.MC_VERSION ?? '1.21.11';
const RCON_PASSWORD = process.env.RCON_PASSWORD ?? 'artmap-e2e';
const BOT_NAME = 'PainterBot';

const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

// Fail loudly if any single step hangs instead of letting CI sit until the
// outer TIMEOUT; 90s is comfortably longer than the whole happy path.
const watchdog = setTimeout(() => {
  console.error('paint bot timed out before completing');
  process.exit(1);
}, 90_000);

const rcon = await Rcon.connect({ host: HOST, port: 25575, password: RCON_PASSWORD });

const bot = mineflayer.createBot({ host: HOST, username: BOT_NAME, version: VERSION });
bot.on('kicked', (reason) => { console.error('kicked:', reason); process.exit(1); });
bot.on('error', (err) => { console.error(err); process.exit(1); });
// ArtMap reports placement/permission problems via chat + action bar; surface
// them so failures here are diagnosable from the CI log.
bot.on('message', (msg) => console.log('[server]', msg.toString()));

// Run an RCON command and echo its reply (give/op feedback is otherwise lost).
async function rconLog(cmd) {
  const reply = await rcon.send(cmd);
  console.log(`[rcon] ${cmd} -> ${reply.trim() || '(no output)'}`);
  return reply;
}

bot.once('spawn', async () => {
  try {
    await paint();
    console.log('paint run complete');
    clearTimeout(watchdog);
    await rcon.end();
    bot.quit();
    process.exit(0);
  } catch (err) {
    console.error('paint run failed:', err);
    process.exit(1);
  }
});

async function paint() {
  console.log('bot spawned');

  // artmap give and easel placement both require artmap.artist/admin; opping
  // the bot is the simplest way to grant them on the throwaway test server.
  await rconLog(`op ${BOT_NAME}`);

  // Flat staging area so the easel has a clear block to sit on and clear air
  // above it. Build a solid floor and clear the space above it, then teleport
  // the bot to a known spot so placement geometry is deterministic.
  const origin = bot.entity.position.floored();
  const floorY = origin.y - 1;
  await rconLog(`fill ${origin.x - 5} ${floorY} ${origin.z - 5} ${origin.x + 5} ${floorY} ${origin.z + 5} stone`);
  await rconLog(`fill ${origin.x - 5} ${floorY + 1} ${origin.z - 5} ${origin.x + 5} ${floorY + 6} ${origin.z + 5} air`);
  // Stand the bot at a fixed spot facing south (+z, yaw 0) on the new floor.
  await rconLog(`tp ${BOT_NAME} ${origin.x + 0.5} ${floorY + 1} ${origin.z + 0.5} 0 0`);
  await sleep(800);

  // Supplies: a real ArtMap easel + canvas (correct NBT) plus a fistful of dye
  // to paint with. These land in the bot's hotbar.
  await rconLog(`artmap give ${BOT_NAME} easel 1`);
  await rconLog(`artmap give ${BOT_NAME} canvas 1`);
  await rconLog(`give ${BOT_NAME} minecraft:red_dye 8`);
  await sleep(1000);

  // ArtMap's easel item is an ARMOR_STAND and its canvas is PAPER (both carry
  // custom NBT, but mineflayer surfaces the base material name), so match those.
  const easelItem = findItem('armor_stand', 'EASEL');
  const canvasItem = findItem('paper', 'CANVAS');
  const dyeItem = bot.inventory.items().find((i) => i.name === 'red_dye');
  if (!dyeItem) throw new Error('bot never received a dye');

  // 1. Place the easel by right-clicking the top face of a floor block ahead.
  // ArtMap requires the click to land on the UP face and spawns the armor-stand
  // easel two blocks above the clicked block. We target a floor block two ahead
  // and look down at its top face so the raycast clearly hits UP.
  await bot.equip(easelItem, 'hand');
  // Click the floor block directly in front of the bot (1 ahead). ArtMap only
  // accepts the click when it lands on the block's UP face, so aim at the top
  // face and confirm via the cursor raycast before sending the interaction.
  const floorPos = new Vec3(origin.x, floorY, origin.z + 1);
  const referenceBlock = await waitForBlock(floorPos, 'placement floor block');

  // Sweep a few aim points until blockAtCursor reports we're hitting the UP face
  // of the target block, then activate. This is robust to small eye/height math.
  const aimPoints = [
    floorPos.offset(0.5, 1.0, 0.4),
    floorPos.offset(0.5, 1.0, 0.5),
    floorPos.offset(0.5, 1.0, 0.3),
    floorPos.offset(0.5, 1.0, 0.2),
  ];
  let aimed = false;
  for (const point of aimPoints) {
    await bot.lookAt(point, true);
    await sleep(150);
    const cursor = bot.blockAtCursor(4);
    if (cursor && cursor.position.equals(floorPos) && cursor.face === 1) { // face 1 = UP (+y)
      aimed = true;
      break;
    }
  }
  if (!aimed) {
    const cursor = bot.blockAtCursor(4);
    console.log('warning: could not confirm UP-face aim; cursor =',
      cursor ? `${cursor.name}@${cursor.position} face=${cursor.face}` : 'none');
  }
  console.log(`placing easel on UP face of ${referenceBlock.name}@${referenceBlock.position}` +
    ` (held=${bot.heldItem ? bot.heldItem.name : 'none'})`);
  // Send the right-click ourselves. mineflayer's activateBlock/placeBlock pin
  // the block_place packet's `sequence` field to 0; on 1.21.x Paper treats a
  // repeated sequence as a stale action and never fires a fresh
  // RIGHT_CLICK_BLOCK PlayerInteractEvent, so ArtMap's easel listener is never
  // entered (it only saw the LEFT_CLICK_AIR arm swing). Writing the packet with
  // a monotonically incrementing sequence makes the server register a real
  // right-click on the UP face, which ArtMap then turns into an easel.
  //
  // The first block_place after the RCON teleport is dropped by the server
  // (position desync — the client hasn't reconciled the tp yet). The length of
  // that window depends on round-trip latency, which is far higher in CI than
  // locally, so a single fixed-delay warm-up isn't reliable. Instead, re-send
  // the placement click and poll for the visible easel STAND to appear, until
  // it does or we exhaust the attempts.
  const easelTarget = floorPos.offset(0.5, 2.0, 0.5);
  const isEaselStand = (e) => e.name === 'armor_stand' && isVisibleEntity(e)
    && e.position.distanceTo(easelTarget) < 3;

  let easel = null;
  for (let attempt = 1; attempt <= 10 && !easel; attempt++) {
    await placeOnUpFace(referenceBlock.position);
    // Poll briefly for the spawn before re-clicking; the easel appears within a
    // few hundred ms once a click actually registers.
    for (let i = 0; i < 10 && !easel; i++) {
      await sleep(250);
      easel = Object.values(bot.entities).find(isEaselStand) || null;
    }
    if (!easel) console.log(`easel not up after attempt ${attempt}; retrying click`);
  }
  if (!easel) throw new Error('timed out waiting for visible easel stand');
  // ArtMap spawns several armor stands per easel (visible STAND + invisible
  // SEAT + invisible small MARKER); only the visible one is the clickable STAND
  // part — getPartType returns SEAT/MARKER (and bails) for the invisible ones.
  console.log('easel (visible STAND) at', easel.position);

  // 2. Mount a canvas: right-click the easel holding the canvas item.
  // Use activateEntityAt (use_entity mouse=2, "interact at"), NOT activateEntity
  // (mouse=0): ArtMap's mount logic runs on PlayerInteractAtEntityEvent, whereas
  // a plain interact fires PlayerInteractEntityEvent — which ArtMap calls with a
  // null clicker and bails out of. The hit point is on the stand body.
  await bot.equip(canvasItem, 'hand');
  await interactAtEasel(easel);
  await sleep(1000);

  // 3. Ride the easel: right-click it again (now holding the dye) to start the
  // painting session. ArtMap seats the player on a hidden armor stand. As with
  // placement, re-click and poll for bot.vehicle rather than trusting a fixed
  // delay, so CI latency doesn't make this flaky.
  await bot.equip(dyeItem, 'hand');
  for (let attempt = 1; attempt <= 10 && !bot.vehicle; attempt++) {
    await interactAtEasel(easel);
    for (let i = 0; i < 10 && !bot.vehicle; i++) await sleep(250);
    if (!bot.vehicle) console.log(`not seated after attempt ${attempt}; retrying`);
  }
  if (!bot.vehicle) throw new Error('bot failed to mount the easel');
  console.log('mounted easel, painting...');

  // 4. Paint: sweep the cursor across the canvas and swing. We aim straight at
  // the canvas (it sits just in front of the seated player) and nudge yaw/pitch
  // over a small grid so strokes spread out. The brush cooldown is ~150ms.
  const baseYaw = bot.entity.yaw;
  const basePitch = bot.entity.pitch;
  let strokes = 0;
  for (let row = -2; row <= 2; row++) {
    for (let col = -2; col <= 2; col++) {
      const yaw = baseYaw + col * 0.06;
      const pitch = basePitch + row * 0.06;
      await bot.look(yaw, pitch, true);
      await sleep(180); // respect the brush stroke cooldown
      bot.swingArm('right');
      bot.attack(easel); // left-click is what ArtMap reads as a paint stroke
      strokes++;
    }
  }
  console.log(`sent ${strokes} strokes`);
  await sleep(500);

  // 5. Dismount: sneaking stands the player up, which makes ArtMap persist the
  // in-progress painting to plugins/ArtMap/Art.db.
  bot.setControlState('sneak', true);
  await sleep(300);
  bot.setControlState('sneak', false);
  await sleep(1500); // give the async persist time to flush to SQLite
  console.log('dismounted');
}

// ── helpers ──────────────────────────────────────────────────────────────────

// True if the entity is NOT invisible. Shared entity flags live at metadata
// index 0; bit 0x20 is the invisible flag. ArtMap's visible armor stand is the
// clickable easel STAND; the invisible ones are the SEAT/MARKER.
function isVisibleEntity(e) {
  const flags = e.metadata && e.metadata[0];
  if (typeof flags !== 'number') return true; // unknown → assume visible
  return (flags & 0x20) === 0;
}

// Right-click the easel stand as an "interact at entity" so the server fires
// PlayerInteractAtEntityEvent (which ArtMap's mount handler listens for), aiming
// at the stand body. activateEntityAt sends use_entity with mouse=2 plus the hit
// offset. We look at the stand first and force the look packet out.
async function interactAtEasel(easel) {
  const hit = easel.position.offset(0, 1.0, 0);
  await bot.lookAt(hit, true);
  await sleep(100);
  await bot.activateEntityAt(easel, hit);
}

// Right-click the UP (+y) face of the block at `pos` by writing the block_place
// packet directly with a fresh, incrementing sequence number. mineflayer's
// built-in activateBlock/placeBlock hardcode sequence:0, which 1.21.x Paper
// rejects as a stale action (no PlayerInteractEvent fires); a monotonic
// sequence makes the server register a genuine right-click on the block.
//
// Packet schema for 1.21.11 (protocol 774):
//   { hand, location, direction, cursorX/Y/Z (f32), insideBlock,
//     worldBorderHit, sequence }
// direction 1 = UP; cursor is the hit point on the face (centre of the top).
let placeSequence = 0;
async function placeOnUpFace(pos) {
  // Look directly at the centre of the block's top face first and force the
  // look packet out, so the server's view of where we're aiming matches the
  // block_place hit data; Paper rejects an interaction whose raycast doesn't
  // reach the claimed block/face.
  await bot.lookAt(pos.offset(0.5, 1.0, 0.5), true);
  await sleep(100);
  bot.swingArm('right');
  bot._client.write('block_place', {
    hand: 0,
    location: pos,
    direction: 1, // UP face (+y)
    cursorX: 0.5,
    cursorY: 1.0,
    cursorZ: 0.5,
    insideBlock: false,
    worldBorderHit: false,
    sequence: ++placeSequence,
  });
}

// Wait until the block at vec is loaded and solid (the RCON fill needs a moment
// to reach the bot's view), returning the real Block so activateBlock can use it.
function waitForBlock(vec, label, timeoutMs = 8000) {
  return new Promise((resolve, reject) => {
    const deadline = Date.now() + timeoutMs;
    const check = () => {
      const block = bot.blockAt(vec);
      if (block && block.boundingBox === 'block') return resolve(block);
      if (Date.now() > deadline) return reject(new Error(`timed out waiting for ${label}`));
      setTimeout(check, 200);
    };
    check();
  });
}

// ArtMap items carry custom NBT but keep their base vanilla material, which is
// what mineflayer reports as i.name. Match on that material.
function findItem(materialName, label) {
  const item = bot.inventory.items().find((i) => i.name === materialName);
  if (!item) {
    const have = bot.inventory.items().map((i) => i.name).join(', ');
    throw new Error(`bot never received a ${label} item (inventory: ${have || 'empty'})`);
  }
  return item;
}

function waitForEntity(predicate, label, timeoutMs = 8000) {
  return new Promise((resolve, reject) => {
    const found = Object.values(bot.entities).find(predicate);
    if (found) return resolve(found);
    const deadline = Date.now() + timeoutMs;
    const timer = setInterval(() => {
      const e = Object.values(bot.entities).find(predicate);
      if (e) {
        clearInterval(timer);
        resolve(e);
      } else if (Date.now() > deadline) {
        clearInterval(timer);
        reject(new Error(`timed out waiting for ${label}`));
      }
    }, 200);
  });
}
