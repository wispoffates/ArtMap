#!/bin/sh
# Integration test for ArtMap plugin.
#
# Starts a Paper Minecraft server in Docker, installs the built ArtMap JAR plus
# its required dependency (ProtocolLib), waits for the server to finish loading,
# and verifies the plugin enabled successfully.
#
# Designed to run inside a GitLab CI job that has a docker:dind service.
# It also works locally when Docker is available. POSIX sh (not bash) so it runs
# directly under the Alpine-based docker:latest CI image, which has no bash.
#
# Environment variables (all optional – defaults are shown):
#   MC_VERSION           Minecraft / Paper version  (default: 1.21.11)
#   PROTOCOLLIB_VERSION  ProtocolLib release tag     (default: 5.3.0)
#   TIMEOUT              Seconds to wait for server  (default: 600)
#   RUN_PAINT_TEST       "true" to also run the mineflayer paint bot against
#                        the booted server and verify strokes reach Art.db
#                        (default: false; the bot lives in integration/bot/)
#   RCON_PASSWORD        RCON password used when the paint test is enabled
#   CI_JOB_ID            Set automatically by GitLab; used to make container /
#                        volume names unique so parallel jobs don't collide.

# -e : exit on error for setup steps (volume, copy, docker run)
# -u : treat unset variables as errors
# (pipefail is bash-only and not used; the polling loop disables -e on purpose so
#  log capture and the case statement always run even when commands fail.)
set -eu

# ──────────────────────────────────────────────────────────────────────────────
# Configuration
# ──────────────────────────────────────────────────────────────────────────────
MC_VERSION="${MC_VERSION:-1.21.11}"
# The itzg image tag selects the server's Java runtime. AnvilGUI's 1.26 NMS
# wrapper (and the ProtocolLib dev-build) ship Java 25 bytecode, which Paper's
# plugin remapper must be able to read, so run on a Java 25 JRE.
SERVER_IMAGE="${SERVER_IMAGE:-itzg/minecraft-server:java25}"
PROTOCOLLIB_VERSION="${PROTOCOLLIB_VERSION:-dev-build}"
TIMEOUT="${TIMEOUT:-600}"
RUN_PAINT_TEST="${RUN_PAINT_TEST:-false}"
RCON_PASSWORD="${RCON_PASSWORD:-artmap-e2e}"

# Unique suffix so parallel CI jobs don't collide on resource names.
JOB_SUFFIX="${CI_JOB_ID:-local-$$}"

CONTAINER_NAME="artmap-server-${JOB_SUFFIX}"
COPY_HELPER="artmap-copy-${JOB_SUFFIX}"
VOLUME_NAME="artmap-plugins-${JOB_SUFFIX}"
BOT_VOLUME_NAME="artmap-bot-${JOB_SUFFIX}"
BOT_HELPER="artmap-bot-copy-${JOB_SUFFIX}"

LOG_FILE="integration/server.log"

PROTOCOLLIB_URL="https://github.com/dmulloy2/ProtocolLib/releases/download/${PROTOCOLLIB_VERSION}/ProtocolLib.jar"

# ──────────────────────────────────────────────────────────────────────────────
# Cleanup – always runs on exit (success or failure)
# ──────────────────────────────────────────────────────────────────────────────
cleanup() {
    echo ""
    echo "--- Cleanup ---"
    docker stop  "$CONTAINER_NAME" 2>/dev/null || true
    docker rm -f "$CONTAINER_NAME" 2>/dev/null || true
    docker rm -f "$COPY_HELPER"    2>/dev/null || true
    docker rm -f "$BOT_HELPER"     2>/dev/null || true
    docker volume rm "$VOLUME_NAME" 2>/dev/null || true
    docker volume rm "$BOT_VOLUME_NAME" 2>/dev/null || true
}
trap cleanup EXIT

# ──────────────────────────────────────────────────────────────────────────────
# Locate the built ArtMap JAR (produced by mvn verify in the build stage)
# ──────────────────────────────────────────────────────────────────────────────
JAR=$(ls plugin/target/ArtMap-*.jar 2>/dev/null \
        | grep -v '\-sources' \
        | grep -v '\-javadoc' \
        | head -1 || true)

if [ -z "$JAR" ]; then
    echo "ERROR: No ArtMap JAR found in plugin/target/. Was the build stage run?"
    exit 1
fi
echo "Using JAR: $JAR"

# ──────────────────────────────────────────────────────────────────────────────
# Create a Docker volume and populate it with the plugin JARs.
#
# NOTE: In Docker-in-Docker (dind) the Docker daemon cannot see the CI job
# container's filesystem via bind mounts.  The workaround is:
#   1. Create a named volume.
#   2. Download ProtocolLib directly into the volume (Alpine + wget).
#   3. docker create a helper container that mounts the volume, then
#      docker cp the ArtMap JAR from the CI workspace into the helper.
#      docker cp works with non-running containers and writes to the volume.
#   4. Mount the volume into the Minecraft server container.
# ──────────────────────────────────────────────────────────────────────────────
echo ""
echo "--- Docker info ---"
docker info

echo ""
echo "--- Setting up plugin volume ---"
docker volume create "$VOLUME_NAME"

# wget -S shows response headers so we can confirm the download succeeded even
# when GitHub redirects the request (302 → S3).  The --no-check-certificate
# flag is a safety net in case the runner's CA bundle is incomplete.
echo "Downloading ProtocolLib ${PROTOCOLLIB_VERSION}..."
docker run --rm \
    -v "${VOLUME_NAME}:/plugins" \
    alpine \
    sh -c "wget -S --no-check-certificate -O /plugins/ProtocolLib.jar '${PROTOCOLLIB_URL}' 2>&1 \
           && echo 'ProtocolLib downloaded successfully.' \
           || { echo 'ERROR: ProtocolLib download failed'; exit 1; }"

echo "Copying ArtMap JAR into volume..."
docker create --name "$COPY_HELPER" -v "${VOLUME_NAME}:/plugins" alpine echo
docker cp "$JAR" "${COPY_HELPER}:/plugins/ArtMap.jar"
docker rm "$COPY_HELPER"
echo "ArtMap JAR copied."

# ──────────────────────────────────────────────────────────────────────────────
# Start the Paper Minecraft server
# ──────────────────────────────────────────────────────────────────────────────
echo ""
echo "--- Starting Paper ${MC_VERSION} server ---"
docker run -d \
    --name "$CONTAINER_NAME" \
    -e EULA=TRUE \
    -e TYPE=PAPER \
    -e VERSION="$MC_VERSION" \
    -e MEMORY=1G \
    -e ONLINE_MODE=FALSE \
    -e ENABLE_RCON="$([ "$RUN_PAINT_TEST" = "true" ] && echo TRUE || echo FALSE)" \
    -e RCON_PASSWORD="$RCON_PASSWORD" \
    -v "${VOLUME_NAME}:/plugins" \
    "$SERVER_IMAGE"

# ──────────────────────────────────────────────────────────────────────────────
# Poll the container logs until the server finishes loading or the timeout fires.
# set -e is disabled here so that transient docker command failures don't abort
# the loop before we have a chance to save the log.
# ──────────────────────────────────────────────────────────────────────────────
echo ""
echo "--- Waiting for server to start (timeout: ${TIMEOUT}s) ---"
START_TIME=$(date +%s)
RESULT="timeout"
mkdir -p integration

set +e
while true; do
    ELAPSED=$(( $(date +%s) - START_TIME ))
    if [ "$ELAPSED" -gt "$TIMEOUT" ]; then
        RESULT="timeout"
        break
    fi

    # Bail out early if the container died unexpectedly
    RUNNING=$(docker inspect "$CONTAINER_NAME" --format '{{.State.Running}}' 2>/dev/null || echo "false")
    if [ "$RUNNING" != "true" ]; then
        RESULT="container_stopped"
        break
    fi

    LOGS=$(docker logs "$CONTAINER_NAME" 2>&1)

    # Plugin JAR rejected at load time
    if echo "$LOGS" | grep -q "Could not load 'plugins/ArtMap.jar'"; then
        RESULT="plugin_load_error"
        break
    fi

    # Server finished starting – now check plugin outcome
    if echo "$LOGS" | grep -q "Done ("; then
        if echo "$LOGS" | grep -qi "Enabling ArtMap"; then
            if echo "$LOGS" | grep -qi "Disabling ArtMap"; then
                RESULT="plugin_disabled"
            else
                RESULT="success"
            fi
        else
            RESULT="plugin_not_loaded"
        fi
        break
    fi

    sleep 10
done
set -e

# ──────────────────────────────────────────────────────────────────────────────
# Optional paint test – drive a mineflayer client against the live server and
# verify the strokes were persisted to ArtMap's database.
#
# Same dind constraint as the plugin JAR: bind mounts don't work, so the bot
# sources are docker-cp'd into a volume and run from a node container that
# shares the default bridge network with the server.
# ──────────────────────────────────────────────────────────────────────────────
if [ "$RUN_PAINT_TEST" = "true" ] && [ "$RESULT" = "success" ]; then
    echo ""
    echo "--- Paint test: running mineflayer bot ---"
    set +e

    SERVER_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' "$CONTAINER_NAME")
    echo "Server IP: ${SERVER_IP}"

    docker volume create "$BOT_VOLUME_NAME"
    docker create --name "$BOT_HELPER" -v "${BOT_VOLUME_NAME}:/bot" alpine echo
    docker cp integration/bot/. "${BOT_HELPER}:/bot/"
    docker rm "$BOT_HELPER"

    docker run --rm \
        -v "${BOT_VOLUME_NAME}:/bot" \
        -w /bot \
        -e MC_HOST="$SERVER_IP" \
        -e MC_VERSION="$MC_VERSION" \
        -e RCON_PASSWORD="$RCON_PASSWORD" \
        node:20-alpine \
        sh -c "npm install --no-audit --no-fund --loglevel=error && node paint-bot.js"
    BOT_EXIT=$?

    if [ "$BOT_EXIT" -ne 0 ]; then
        RESULT="paint_bot_failed"
    else
        echo ""
        echo "--- Paint test: verifying Art.db ---"
        docker cp "${CONTAINER_NAME}:/data/plugins/ArtMap/Art.db" integration/Art.db
        if [ ! -f integration/Art.db ]; then
            RESULT="paint_verify_failed"
        else
            # Painted (or in-progress) maps land in the maps table as blobs.
            MAPS=$(docker run --rm -i alpine \
                sh -c 'apk add -q sqlite && cat > /tmp/Art.db && sqlite3 /tmp/Art.db "SELECT count(*) FROM maps WHERE length(map) > 0;"' \
                < integration/Art.db)
            echo "Maps with painted data: ${MAPS:-0}"
            if [ "${MAPS:-0}" -gt 0 ] 2>/dev/null; then
                RESULT="success"
            else
                RESULT="paint_verify_failed"
            fi
        fi
    fi
    set -e
fi

# ──────────────────────────────────────────────────────────────────────────────
# Capture full server log (saved as a CI artifact for debugging)
# ──────────────────────────────────────────────────────────────────────────────
echo ""
echo "--- Saving server log to ${LOG_FILE} ---"
docker logs "$CONTAINER_NAME" > "$LOG_FILE" 2>&1 || true

# ──────────────────────────────────────────────────────────────────────────────
# Report result
# ──────────────────────────────────────────────────────────────────────────────
echo ""
echo "=============================="
echo " Integration Test: ${RESULT}"
echo "=============================="
echo ""

case "$RESULT" in
    success)
        echo "PASS: ArtMap loaded and enabled successfully on Paper ${MC_VERSION}."
        if [ "$RUN_PAINT_TEST" = "true" ]; then
            echo "PASS: paint bot strokes were persisted to Art.db."
        fi
        exit 0
        ;;
    paint_bot_failed)
        echo "FAIL: the paint bot exited with an error (see output above)."
        echo "      Check ${LOG_FILE} for the server side."
        exit 1
        ;;
    paint_verify_failed)
        echo "FAIL: the paint bot ran but no painted map data was found in Art.db."
        echo "      Check ${LOG_FILE} for the server side."
        exit 1
        ;;
    plugin_disabled)
        echo "FAIL: ArtMap was disabled by the server after loading."
        echo "      Check ${LOG_FILE} for the cause."
        cat "$LOG_FILE"
        exit 1
        ;;
    plugin_not_loaded)
        echo "FAIL: Server started but ArtMap did not appear in the plugin list."
        echo "      Check ${LOG_FILE} for the cause."
        cat "$LOG_FILE"
        exit 1
        ;;
    plugin_load_error)
        echo "FAIL: ArtMap JAR could not be loaded by the server."
        echo "      Check ${LOG_FILE} for the cause."
        cat "$LOG_FILE"
        exit 1
        ;;
    container_stopped)
        echo "FAIL: Minecraft server container stopped unexpectedly."
        echo "      Check ${LOG_FILE} for the cause."
        cat "$LOG_FILE"
        exit 1
        ;;
    timeout)
        echo "FAIL: Server did not finish starting within ${TIMEOUT} seconds."
        echo "      Check ${LOG_FILE} for the cause."
        cat "$LOG_FILE"
        exit 1
        ;;
    *)
        echo "FAIL: Unknown result '${RESULT}'."
        cat "$LOG_FILE"
        exit 1
        ;;
esac
