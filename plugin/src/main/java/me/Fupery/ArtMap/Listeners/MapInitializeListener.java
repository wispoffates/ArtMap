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
        Integer mapId = event.getMap().getId();
        if(!mapQueue.contains(mapId)) { //check that we are not inserting duplicates
            mapQueue.add(mapId);
        }
    }

    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }

    @Override
    public void run() {
        int stop = 0;
        while (!mapQueue.isEmpty()) {
            Integer mapId = mapQueue.poll();
            if(mapId == null) {
                return; //this shouldn't happen but lets be safe
            }
            try {
                stop = 0;
                if (!ArtMap.instance().getArtDatabase().containsArtwork(mapId))
                    return;
                stop = 1;
                Map map = new Map(mapId);
                stop = 2;
                ArtMap.instance().getArtDatabase().restoreMap(map, true, false);
            } catch (Exception e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Error with map restore! Stop="+stop, e);
            }
        }
    }
}
