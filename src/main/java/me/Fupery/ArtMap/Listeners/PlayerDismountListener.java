package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerDismountListener implements RegisteredListener {

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (ArtMap.instance().getArtistHandler().containsPlayer(player)) {
            try {
                ArtMap.instance().getArtistHandler().removePlayer(player);
            } catch (SQLException | IOException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
                player.sendMessage("Error Saving Artwork check logs.");
                return; 
            }
        }
    }

    @Override
    public void unregister() {
        EntityDismountEvent.getHandlerList().unregister(this);
    }
}
