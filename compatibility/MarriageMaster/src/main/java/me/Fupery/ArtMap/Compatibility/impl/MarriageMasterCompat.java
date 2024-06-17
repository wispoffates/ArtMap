package me.Fupery.ArtMap.Compatibility.impl;

import java.util.logging.Level;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Events.GiftEvent;
import me.Fupery.ArtMap.api.IArtMap;
import me.Fupery.ArtMap.api.Compatability.EventListener;
import net.md_5.bungee.api.ChatColor;

public class MarriageMasterCompat implements EventListener {
    private boolean loaded = false;
    private IArtMap artmap;

    public MarriageMasterCompat(IArtMap artmap) {
        this.artmap = artmap;
        this.loaded = true;
        //register listeners
        artmap.getServer().getPluginManager().registerEvents(this, artmap);
    }

    @EventHandler
    public void onGiftEvent(GiftEvent event) {
        try {
            OfflinePlayer partner1 = event.getMarriageData().getPartner1().getPlayer();
            OfflinePlayer partner2 = event.getMarriageData().getPartner2().getPlayer();
            if(partner1.isOnline()) {
                if(artmap.getArtistHandler().containsPlayer(partner1.getPlayer()) 
                    && artmap.getArtistHandler().getCurrentSession(partner1.getPlayer()).isInArtKit()) {
                        event.setCancelled(true);
                        partner1.getPlayer().sendMessage(ChatColor.RED+" You cannot send gifts while using the Artkit!");
                }
            }
            if(partner2.isOnline()) {
                if(artmap.getArtistHandler().containsPlayer(partner2.getPlayer()) 
                    && artmap.getArtistHandler().getCurrentSession(partner2.getPlayer()).isInArtKit()) {
                        event.setCancelled(true);
                        partner2.getPlayer().sendMessage(ChatColor.RED+" You cannot send gifts while using the Artkit!");
                }
            }
        } catch (Exception e) {
            artmap.getLogger().log(Level.SEVERE, "Error interteracting with MarriageMaster!", e);
        }
    }

    @Override
    public void unregister() {
        GiftEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
