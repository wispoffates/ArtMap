package me.Fupery.ArtMap.Listeners;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatibility.CompatibilityManager;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.Exception.ArtMapException;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.Utils.LocationHelper;

class PlayerInteractListener implements RegisteredListener {

    private static BlockFace getFacing(Player player) {
        int yaw = ((int) player.getLocation().getYaw()) % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;
        }
        if (yaw >= 45 && yaw < 135) {
            return BlockFace.EAST;
        }
        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;
        }
        if (yaw >= 225 && yaw < 315) {
            return BlockFace.WEST;
        }
        return BlockFace.NORTH;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (ArtMap.instance().getPreviewManager().endPreview(event.getPlayer()))
            event.setCancelled(true);

        // Don't place paint brushes in the world
        if (ArtMaterial.getCraftItemType(event.getItem()) == ArtMaterial.PAINT_BRUSH) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Lang.PAINTBRUSH_GROUND.get());
                return;
            }
        }

        if (!ArtMaterial.EASEL.isValidMaterial(event.getItem()) || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        event.setCancelled(true);

        if (event.getBlockFace() != BlockFace.UP) {
            return;
        }
        Player player = event.getPlayer();
        Location baseLocation = event.getClickedBlock().getLocation().clone().add(.5, 1.25, .5);
        Location easelLocation = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
        CompatibilityManager compat = ArtMap.instance().getCompatManager();
        if (!player.hasPermission("artmap.artist") || !compat.checkBuildAllowed(player, baseLocation)
                || !compat.checkBuildAllowed(player, easelLocation)) {
            Lang.ActionBar.NO_PERM_ACTION.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        BlockFace facing = getFacing(player);
        Location frameBlock = new LocationHelper(easelLocation).shiftTowards(facing);

        if (!easelLocation.getBlock().isEmpty() || !baseLocation.getBlock().isEmpty()
                || !frameBlock.getBlock().isEmpty() || Easel.checkForEasel(easelLocation)) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        Easel easel = Easel.spawnEasel(easelLocation, facing);

        // remove 1 easel from either hand
        removeEaselFromHandle(player);

        if (easel == null) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
        } else {
            EaselEffect.SPAWN.playEffect(new LocationHelper(baseLocation).shiftTowards(facing, .5));
        }
    }

    private void removeEaselFromHandle(Player player) {
        // check main hand
        if (ArtMaterial.EASEL.isValidMaterial(player.getInventory().getItemInMainHand())) {
            ItemStack items = player.getInventory().getItemInMainHand();
            if (items.getAmount() > 1) {
                items.setAmount(items.getAmount() - 1);
            } else {
                items = null;
            }
            player.getInventory().setItemInMainHand(items);
            // check off hand
        } else if (ArtMaterial.EASEL.isValidMaterial(player.getInventory().getItemInOffHand())) {
            ItemStack items = player.getInventory().getItemInOffHand();
            if (items.getAmount() > 1) {
                items.setAmount(items.getAmount() - 1);
            } else {
                items = null;
            }
            player.getInventory().setItemInOffHand(items);
        }
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {
        final ItemStack item = event.getCursor();

        if (event.getClick() != ClickType.CREATIVE || event.getClickedInventory() == null
                || item.getType() != Material.FILLED_MAP) {
            return;
        }

        ArtMap.instance().getScheduler().ASYNC.run(() -> {

            ItemMeta meta = item.getItemMeta();

            if (!meta.hasLore()) {

                MapArt art = null;
                try {
                    art = ArtMap.instance().getArtDatabase().getArtwork(ItemUtils.getMapID(item));
                } catch (SQLException e) {
                    ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
					event.getWhoClicked().sendMessage("Error Retrieving Artwork check logs.");
            		return; 
                } catch (ArtMapException e) {
                    return; //some maps do not have mapviews and we don't want to spam about it here.
                }

                if (art != null) {

                    ItemStack correctLore = art.getMapItem();
                    event.getClickedInventory().setItem(event.getSlot(), correctLore);
                }
            }
        });
    }

	@Override
    public void unregister() {
        PlayerInteractEvent.getHandlerList().unregister(this);
        InventoryCreativeEvent.getHandlerList().unregister(this);
    }
}
