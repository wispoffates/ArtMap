# Pre-baked Paper server image for the ArtMap integration test.
#
# The stock itzg/minecraft-server image downloads the Mojang vanilla server jar
# ("Downloading mojang_<version>.jar") and patches Paper against it on first
# boot. That download comes from Mojang's CDN and is slow/flaky from CI runners,
# which makes the integration job time out waiting for the server to finish
# starting.
#
# This image does that one-time work at *build* time instead. Because itzg
# declares /data as a VOLUME (writes to it during `docker build` are discarded),
# we bake into a plain directory (/baked-data) and seed /data from it at
# container start, before itzg's own entrypoint runs. The runtime boot then
# starts from the patched jar + generated world with no Mojang CDN round-trip.
#
# Plugins are intentionally NOT baked in — they change every pipeline and are
# still mounted into /plugins at runtime by run-integration-test.sh.
#
# Build args let CI bake the same MC_VERSION the test uses:
#   docker build --build-arg MC_VERSION=1.21.11 -t <ref> integration/server-image
ARG BASE_IMAGE=itzg/minecraft-server:java25
FROM ${BASE_IMAGE}

ARG MC_VERSION=1.21.11

ENV EULA=TRUE \
    TYPE=PAPER \
    VERSION=${MC_VERSION} \
    MEMORY=1G \
    ONLINE_MODE=FALSE

COPY bake-server.sh /bake-server.sh
COPY seed-and-start.sh /seed-and-start.sh

# Boot once (data dir pointed at /baked-data so it survives the build, since
# /data is a discarded VOLUME) to download+patch the Mojang jar and generate the
# world, then stop. /baked-data is committed into the image layer.
RUN sh /bake-server.sh

# On container start, seed /data from /baked-data (only if /data is empty), then
# hand off to the stock itzg entrypoint.
ENTRYPOINT ["/seed-and-start.sh"]
