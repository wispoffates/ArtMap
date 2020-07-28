package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Exception.HeadFetchException;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

class PlayerJoinEventListener implements RegisteredListener {
    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent event) {
        // update the playes skin in the cache
        ArtMap.instance().getScheduler().ASYNC.run(() -> {
            try {
                ArtMap.instance().getHeadsCache().updateCache(event.getPlayer().getUniqueId());
            } catch (HeadFetchException e) {
               //Too noisy going to drop the skin texture errors hear as any major issues should be caught and logged at startup.
            }
        });
        if(ArtMap.instance().isDBUpgradeNeeded() && event.getPlayer().hasPermission("artmap.admin")) {
            event.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Artmap: Old Artmap database needs coverted.  Please use /artmap covert");
        }   
    }

    @Override
    public void unregister() {
        PlayerJoinEvent.getHandlerList().unregister(this);
    }
}