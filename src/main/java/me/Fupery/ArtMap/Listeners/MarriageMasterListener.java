package me.Fupery.ArtMap.Listeners;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.Events.GiftEvent;
import me.Fupery.ArtMap.ArtMap;

public class MarriageMasterListener implements RegisteredListener {

    @EventHandler
    public void onGiftEvent(GiftEvent event) {
        try {
            OfflinePlayer partner1 = event.getMarriageData().getPartner1().getPlayer();
            OfflinePlayer partner2 = event.getMarriageData().getPartner1().getPlayer();
            if(partner1.isOnline()) {
                if(ArtMap.instance().getArtistHandler().containsPlayer(partner1.getPlayer()) 
                    && ArtMap.instance().getArtistHandler().getCurrentSession(partner1.getPlayer()).isInArtKit()) {
                        event.setCancelled(true);
                        partner1.getPlayer().sendMessage(ChatColor.RED+" You cannot send gifts while using the Artkit!");
                }
            }
            if(partner2.isOnline()) {
                if(ArtMap.instance().getArtistHandler().containsPlayer(partner2.getPlayer()) 
                    && ArtMap.instance().getArtistHandler().getCurrentSession(partner2.getPlayer()).isInArtKit()) {
                        event.setCancelled(true);
                        partner2.getPlayer().sendMessage(ChatColor.RED+" You cannot send gifts while using the Artkit!");
                }
            }
        } catch (Exception e) {
            ArtMap.instance().getLogger().log(Level.SEVERE, "Error interteracting with MarriageMaster!", e);
        }
    }

    @Override
    public void unregister() {
        GiftEvent.getHandlerList().unregister(this);
    }
}
