package me.Fupery.ArtMap.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ItemUtils;

class InventoryInteractListener implements RegisteredListener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkPreviewing(event.getPlayer(), event);
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
		checkPreviewing((Player) event.getWhoClicked(), event);
		checkArtKitPagination(((Player) event.getWhoClicked()), event.getCurrentItem(), event);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (ArtMap.getPreviewManager().endPreview(event.getPlayer())) event.getItemDrop().remove();
		if (ArtMap.getArtistHandler().getCurrentSession(event.getPlayer()) != null) {
			if (ArtMap.getArtistHandler().getCurrentSession(event.getPlayer()).isInArtKit()) {
				event.getItemDrop().remove();
			}
		}
        if (isKitDrop(event.getPlayer(), event.getItemDrop().getItemStack(), event)) {
            event.getItemDrop().remove();
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtMap.getPreviewManager().endPreview(player)) event.setCancelled(true);
    }

	private void checkArtKitPagination(Player player, ItemStack itemStack, Cancellable event) {
		if (ArtMap.getArtistHandler().containsPlayer(player)) {
			if (ItemUtils.hasKey(itemStack, "Artkit:Next")) {
				event.setCancelled(true);
				ArtMap.getArtistHandler().getCurrentSession(player).nextKitPage(player);
			}
			if (ItemUtils.hasKey(itemStack, "Artkit:Back")) {
				event.setCancelled(true);
				ArtMap.getArtistHandler().getCurrentSession(player).prevKitPage(player);
			}
		}
	}

    private boolean isKitDrop(Player player, ItemStack itemStack, Cancellable event) {
        if (ArtMap.getArtistHandler().containsPlayer(player)) {
			if (ItemUtils.hasKey(itemStack, ArtItem.KIT_KEY) || ItemUtils.hasKey(itemStack, "Artkit:Next")
					|| ItemUtils.hasKey(itemStack, "Artkit:Back")) {
				return true;
			}
        }
        return false;
    }

    @Override
    public void unregister() {
        PlayerItemHeldEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }
}
