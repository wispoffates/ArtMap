package me.Fupery.ArtMap.Listeners;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Database.Map;

public class MapInitializeListener implements RegisteredListener {

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        try {
            int mapId = event.getMap().getId();
            // Bukkit.getLogger().info("Map initialize: " + mapId);//TODO remove logging
            ArtMap.getScheduler().ASYNC.run(() -> {
                if (!ArtMap.getArtDatabase().getMapTable().containsMap(mapId))
                    return;
                Map map = new Map(mapId);
                ArtMap.getArtDatabase().restoreMap(map);
                // Bukkit.getLogger().info("Map restored: " + mapId);//TODO remove logging
            });
        } catch (Exception e) {
            ArtMap.instance().getLogger().log(Level.SEVERE, "Error with map restore!", e);
        }
    }

    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }
}
