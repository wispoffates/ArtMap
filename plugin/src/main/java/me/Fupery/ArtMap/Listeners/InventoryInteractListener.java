package me.Fupery.ArtMap.Listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
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
        
        //prevent Artkit items from going into inventories they shouldn't like
        //ender chest and crafting table but allow it in creative mode
        if( event.getWhoClicked().getGameMode() != GameMode.CREATIVE &&
            event.getInventory().getType() != InventoryType.PLAYER && 
            event.getInventory().getType() != InventoryType.CRAFTING &&
            event.getInventory().getType() != InventoryType.CREATIVE) {
            if(isKitDrop((Player) event.getWhoClicked(), event.getCurrentItem())) {
                event.setCancelled(true);
            }
        }
        // lock armor slots while at the easel
		else if (ArtMap.instance().getConfiguration().FORCE_ART_KIT && 
            event.getWhoClicked().hasPermission("artmap.artkit") &&
			ArtMap.instance().getArtistHandler().getCurrentSession((Player) event.getWhoClicked()) != null &&
			ArtMap.instance().getArtistHandler().getCurrentSession((Player) event.getWhoClicked()).isInArtKit() &&
			event.getSlotType() == InventoryType.SlotType.ARMOR){
			event.setCancelled(true);
		}
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (ArtMap.instance().getPreviewManager().endPreview(event.getPlayer())) event.getItemDrop().remove();
		if (ArtMap.instance().getArtistHandler().getCurrentSession(event.getPlayer()) != null) {
			if (ArtMap.instance().getArtistHandler().getCurrentSession(event.getPlayer()).isInArtKit()) {
				event.getItemDrop().remove();
			}
		}
        if (isKitDrop(event.getPlayer(), event.getItemDrop().getItemStack())) {
            event.getItemDrop().remove();
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent e) {
        if(e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (ArtMap.instance().getArtistHandler().getCurrentSession(player) != null) {
                if (ArtMap.instance().getArtistHandler().getCurrentSession(player).isInArtKit()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtMap.instance().getPreviewManager().endPreview(player)) {
            event.setCancelled(true);
        }
    }

	private void checkArtKitPagination(Player player, ItemStack itemStack, Cancellable event) {
		if (ArtMap.instance().getArtistHandler().containsPlayer(player)) {
			if (ItemUtils.hasKey(itemStack, "Artkit:Next")) {
				event.setCancelled(true);
				ArtMap.instance().getArtistHandler().getCurrentSession(player).nextKitPage(player);
			}
			if (ItemUtils.hasKey(itemStack, "Artkit:Back")) {
				event.setCancelled(true);
				ArtMap.instance().getArtistHandler().getCurrentSession(player).prevKitPage(player);
			}
		}
	}

    private boolean isKitDrop(Player player, ItemStack itemStack) {
        if (ArtMap.instance().getArtistHandler().containsPlayer(player)) {
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
