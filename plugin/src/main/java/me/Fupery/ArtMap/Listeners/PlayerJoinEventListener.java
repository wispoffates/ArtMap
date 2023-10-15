package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

class PlayerJoinEventListener implements RegisteredListener {
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        // update the playes skin in the cache
        ArtMap.instance().getScheduler().ASYNC.run(() -> 
            ArtMap.instance().getHeadsCache().updateCache(event.getPlayer().getUniqueId())
        );
        if(ArtMap.instance().isDBUpgradeNeeded() && event.getPlayer().hasPermission("artmap.admin")) {
            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Artmap: Old Artmap database needs coverted.  Please use /artmap covert");
        }   
    }

    @Override
    public void unregister() {
        PlayerJoinEvent.getHandlerList().unregister(this);
    }
}