package me.Fupery.ArtMap.Compatibility.impl;

import java.util.logging.Level;

import com.Zrips.CMI.events.CMIAfkKickEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import me.Fupery.ArtMap.api.IArtMap;
import me.Fupery.ArtMap.api.Compatability.EventListener;

public class CMICompat implements EventListener {
    private boolean loaded = false;
    private IArtMap artmap;

    public CMICompat(IArtMap artmap) {
        this.artmap = artmap;
        this.loaded = true;
        //register listeners
        artmap.getServer().getPluginManager().registerEvents(this, artmap);
    }

    @EventHandler
    public void onAFKKickEvent(CMIAfkKickEvent event) {
        try {
            Player player = event.getPlayer();
            if(artmap.getArtistHandler().containsPlayer(player) && player.hasPermission("artmap.ignore.afk")) {
                    event.setCancelled(true);
            }
        } catch (Exception e) {
            artmap.getLogger().log(Level.SEVERE, "Error interteracting with MarriageMaster!", e);
        }
    }

    @Override
    public void unregister() {
        CMIAfkKickEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
