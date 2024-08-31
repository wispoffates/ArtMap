package me.Fupery.ArtMap.Listeners;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.Map;

public class MapInitializeListener extends BukkitRunnable implements RegisteredListener {

    private static final Queue<Integer> mapQueue = new LinkedList<>();
    private static BukkitRunnable runnable = null;

    private synchronized void createRunner() {
        if(runnable == null) {
            ArtMap.instance().getLogger().info("Created Artmap map initialize queue task.");
            this.runTaskTimerAsynchronously(ArtMap.instance(), 0, 20);
            runnable = this;
        }
    }

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        createRunner();
        int mapId = event.getMap().getId();
        mapQueue.add(mapId);
    }

    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }

    @Override
    public void run() {
        while (!mapQueue.isEmpty()) {
            Integer mapId = mapQueue.poll();
            try {
                if (!ArtMap.instance().getArtDatabase().containsArtwork(mapId))
                    return;
                Map map = new Map(mapId);
                ArtMap.instance().getArtDatabase().restoreMap(map, true, false);
            } catch (Exception e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Error with map restore!", e);
            }
        }
    }
}
