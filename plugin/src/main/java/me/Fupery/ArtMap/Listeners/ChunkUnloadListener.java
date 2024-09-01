package me.Fupery.ArtMap.Listeners;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Utils.ChunkLocation;

class ChunkUnloadListener implements RegisteredListener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        ConcurrentMap<Location,Easel> easels = ArtMap.instance().getEasels();
        if (!easels.isEmpty()) {
            ChunkLocation chunk = new ChunkLocation(event.getChunk());
            ArtMap.instance().getScheduler().ASYNC.run(() -> {
                for (Entry<Location, Easel> entry : easels.entrySet()) {
                    Easel easel = entry.getValue();
					if (easel != null && easel.getChunk() != null && easel.getChunk().equals(chunk)) {
                        easels.remove(entry.getKey());
                    }
                }
            });
        }
    }

    @Override
    public void unregister() {
        ChunkUnloadEvent.getHandlerList().unregister(this);
    }
}
