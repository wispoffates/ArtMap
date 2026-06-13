# ArtMap integration tests

Boots a real Paper server in Docker with the built ArtMap jar + ProtocolLib,
and runs in two phases:

1. **Load check** (always): waits for the server to finish starting and
   verifies ArtMap enabled without being disabled or rejected. This is what
   the `integration` CI job runs on every pipeline.
2. **Paint test** (`RUN_PAINT_TEST=true`, skeleton): runs the mineflayer bot
   in `bot/` against the live server to exercise the real packet path —
   ProtocolLib channel injection, NMS map storage — then pulls
   `plugins/ArtMap/Art.db` out of the container and asserts painted map data
   landed. In CI this is the manual `integration:paint` job.

Everything downstream of packet decoding (cursor math, brushes, flood fill,
renderer, compression, SQLite) is already covered in-JVM by
`plugin/src/test/java/me/Fupery/ArtMap/Painting/PaintingIntegrationTest.java`,
which runs in the normal test suite. The paint phase here only needs to prove
that a real client's packets arrive and persist.

## Files

- `run-integration-test.sh` — CI entry point; handles the docker-in-docker
  volume workarounds, both phases, and result reporting.
- `bot/` — mineflayer client bot. **Skeleton**: join/RCON staging works;
  easel placement and the painting loop are TODO, and the bot exits non-zero
  until they're implemented so CI can't report a false pass.
- `docker-compose.yml` — local convenience equivalent of the CI setup, for
  developing the bot against a server you can join with a real client too.

## Running locally

```bash
# CI-equivalent (needs Docker):
mvn -pl plugin -am package
RUN_PAINT_TEST=true ./integration/run-integration-test.sh   # from repo root

# Or keep a server running while iterating on the bot:
cd integration
mkdir -p server-data/plugins && cp ../plugin/target/ArtMap-*.jar server-data/plugins/
# download ProtocolLib into server-data/plugins/ as well
docker compose up -d
(cd bot && npm install && node paint-bot.js)
```

## Known constraints

- **Protocol version coupling**: mineflayer supports specific Minecraft
  versions and lags new releases (relevant for the 26.x scheme). The
  `integration:paint` CI job pins `MC_VERSION` to a bot-supported version
  independently of the load-check job.
- **The AnvilGUI save dialog** (title entry) is awkward to drive over the
  protocol. The bot instead relies on the easel persisting in-progress
  strokes to `Art.db` on dismount, which is what the verify step checks.
