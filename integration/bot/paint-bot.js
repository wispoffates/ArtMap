// E2E paint bot (skeleton): joins the Paper server booted by
// run-integration-test.sh (or docker-compose.yml locally), sets up an easel,
// mounts it and sends the look/swing packets a painting player would.
//
// The in-JVM PaintingIntegrationTest already covers everything downstream of
// packet decoding, so the goal here is only to prove the real packet path:
// join -> mount easel -> strokes arrive -> dismount persists to Art.db.

import mineflayer from 'mineflayer';
import { Rcon } from 'rcon-client';

const HOST = process.env.MC_HOST ?? 'localhost';
const VERSION = process.env.MC_VERSION ?? '1.21.4';
const RCON_PASSWORD = process.env.RCON_PASSWORD ?? 'artmap-e2e';

const rcon = await Rcon.connect({ host: HOST, port: 25575, password: RCON_PASSWORD });

const bot = mineflayer.createBot({ host: HOST, username: 'PainterBot', version: VERSION });
bot.once('spawn', async () => {
  console.log('bot spawned');

  // Flat staging area + supplies, server-side so the bot needs no inventory work.
  const p = bot.entity.position.floored();
  await rcon.send(`fill ${p.x - 4} ${p.y - 1} ${p.z - 4} ${p.x + 4} ${p.y - 1} ${p.z + 4} stone`);
  await rcon.send(`fill ${p.x - 4} ${p.y} ${p.z - 4} ${p.x + 4} ${p.y + 3} ${p.z + 4} air`);
  // ArtMap items carry custom names/lore; crafting them by hand over protocol is
  // painful. Easiest path: give vanilla materials and craft, or pre-place the
  // easel with a setup player/command. TODO: settle on one approach:
  //   1. `give` paper+sticks and use bot.craft() with ArtMap's shaped recipes, or
  //   2. an RCON-driven setup plugin/datapack that spawns a ready easel.
  console.log('TODO: place easel + canvas at', p.offset(2, 0, 0));

  // Mount: right-click the easel's armor stand.
  // const easel = bot.nearestEntity(e => e.name === 'armor_stand');
  // await bot.activateEntity(easel);

  // Paint strokes: hold a dye, look across the canvas, swing.
  // for (let i = 0; i < 20; i++) {
  //   await bot.look(yawForColumn(i), pitchForRow(i), true);
  //   bot.swingArm('right');           // left-click stroke
  //   await sleep(200);                // brush cooldown is 150ms
  // }

  // Dismount persists the in-progress painting to plugins/ArtMap/Art.db.
  // bot.setControlState('sneak', true);

  console.log('skeleton run complete');
  await rcon.end();
  bot.quit();
  // Exit non-zero until the TODOs above are filled in, so CI reports the paint
  // phase as not-yet-implemented rather than a false PASS with an empty Art.db.
  process.exit(2);
});

bot.on('kicked', (reason) => { console.error('kicked:', reason); process.exit(1); });
bot.on('error', (err) => { console.error(err); process.exit(1); });
