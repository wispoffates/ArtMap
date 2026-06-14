#!/usr/bin/env bash
# Container entrypoint for the pre-baked ArtMap server image.
#
# /data is a volume, so the server state baked at build time lives in /baked-data
# instead. Seed /data from it on first start (when /data is empty), then hand off
# to the stock itzg entrypoint. Seeding the patched Mojang jar + generated world
# this way skips the slow "Downloading mojang_<version>.jar" step at runtime.
set -eu

# Consider /data unseeded if the patched-jar dir is absent — i.e. a fresh volume.
# If a caller mounted real data (versions/ present) we leave it untouched.
if [ ! -d /data/versions ]; then
    echo "[seed] /data is empty; seeding from baked /baked-data..."
    # Copy contents (including dotfiles) without clobbering an explicit mount.
    cp -a /baked-data/. /data/
    echo "[seed] seed complete."
else
    echo "[seed] /data already populated; skipping seed."
fi

# Hand off to the real itzg entrypoint.
exec /image/scripts/start
