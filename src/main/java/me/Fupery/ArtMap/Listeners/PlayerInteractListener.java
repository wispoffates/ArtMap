package me.Fupery.ArtMap.Listeners;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.Preview;
import me.Fupery.InventoryMenu.Utils.SoundCompat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PlayerInteractListener implements Listener {

    private static BlockFace getFacing(Player player) {
        int yaw = ((int) player.getLocation().getYaw()) % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.WEST;

        } else if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;

        } else if (yaw >= 45 && yaw < 135) {
            return BlockFace.EAST;

        } else return BlockFace.NORTH;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (ArtMap.getPreviewing().containsKey(event.getPlayer())) {
            event.setCancelled(true);
            Preview.stop(event.getPlayer());
        }

        if (!ArtMaterial.EASEL.isValidMaterial(event.getItem())
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
                || event.isCancelled()) {
            return;
        }
        event.setCancelled(true);

        if (!event.getBlockFace().equals(BlockFace.UP)) {
            return;
        }
        Player player = event.getPlayer();

        if (!player.hasPermission("artmap.artist")) {
            ArtMap.getLang().sendMsg("NO_PERM", player);
            return;
        }
        Location easelLocation = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
        BlockFace facing = getFacing(player);

        if (easelLocation.getBlock().getType() != Material.AIR || Easel.checkForEasel(easelLocation)) {
            ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_INVALID_POS.send(player);
            SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
            return;
        }
        Easel easel = Easel.spawnEasel(easelLocation, facing);
        ItemStack item = player.getItemInHand().clone();
        item.setAmount(1);

        player.getInventory().removeItem(item);

        if (easel == null) {
            ArtMap.getLang().ACTION_BAR_MESSAGES.EASEL_INVALID_POS.send(player);
            SoundCompat.ENTITY_ARMORSTAND_BREAK.play(player);
        }
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {
        final ItemStack item = event.getCursor();

        if (event.getClick() != ClickType.CREATIVE || event.getClickedInventory() == null
                || item == null || item.getType() != Material.MAP) {
            return;
        }

        ArtMap.getTaskManager().ASYNC.run(() -> {

            ItemMeta meta = item.getItemMeta();

            if (!meta.hasLore()) {

                MapArt art = ArtMap.getArtDatabase().getArtwork(item.getDurability());

                if (art != null) {

                    ItemStack correctLore = art.getMapItem();
                    event.getClickedInventory().setItem(event.getSlot(), correctLore);
                }
            }
        });
    }
}
