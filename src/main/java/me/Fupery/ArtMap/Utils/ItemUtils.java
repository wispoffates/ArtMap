package me.Fupery.ArtMap.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

public class ItemUtils {

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
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        else if (lore.contains(key)) return item;
        lore.add(key);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

	/**
	 * Handle 1.13 maps having their ID as part of the meta data.
	 * 
	 * @param item The Map Item to get the id of.
	 * @return The ID of the map or -1 if something went wrong.
	 */
	public static short getMapID(ItemStack item) {
		if (item.getType() != Material.FILLED_MAP) {
			return -1;
		}

		MapMeta meta = (MapMeta) item.getItemMeta();
		return (short) meta.getMapId();
	}
}
