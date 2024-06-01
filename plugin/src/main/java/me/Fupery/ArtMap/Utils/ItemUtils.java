package me.Fupery.ArtMap.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import me.Fupery.ArtMap.api.Exception.ArtMapException;

public class ItemUtils {

    protected ItemUtils() {
        //this is a static class so hide the constructor
    }

    public static void giveItem(Player player, ItemStack item) {
        ItemStack leftOver = player.getInventory().addItem(item).get(0);
        if (leftOver != null && leftOver.getAmount() > 0)
            player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
    }

    public static boolean hasKey(ItemStack itemStack, String key) {
        if (itemStack != null && itemStack.hasItemMeta()) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            return itemMeta.hasLore() && itemMeta.getLore().contains(key);
        }
        return false;
    }

    public static ItemStack addKey(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) {
            return null;
        }
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        } else if (lore.contains(key)) {
            return item;
        } 
        lore.add(key);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

	/**
     * Handle 1.13 maps having their ID as part of the meta data.
     * 
     * @param item The Map Item to get the id of.
     * @return The ID of the map or Empty if the mapview isnt present.
     * @throws ArtMapException 
     */
    public static Optional<Integer> getMapID(ItemStack item) throws ArtMapException {
		if (item.getType() != Material.FILLED_MAP) {
			throw new ArtMapException("Tried to get the map id of an item that is not a map.");
		}

        MapMeta meta = (MapMeta) item.getItemMeta();
        if(null == meta) {
            throw new ArtMapException("Tried to get the map id but the map doesn't have a map meta?");
        }
        if(meta.hasMapView() && meta.getMapView() != null) {
            return Optional.of(meta.getMapView().getId());
        } else {
            return Optional.empty();
        }
	}
}
