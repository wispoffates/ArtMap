package me.Fupery.ArtMap.Easel;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;

/**
 * Represents a painting canvas. Extends ItemStack so that information can be
 * retrieved when it is pulled off the easel.
 *
 */
public class Canvas extends ItemStack {

    public Canvas(Map map) {
		this(map.getMapId());
    }

	protected Canvas(short mapId) {
		super(Material.MAP, 1, mapId);
    }

	public static Canvas getCanvas(ItemStack item) {
        if (item == null || item.getType() != Material.MAP) return null;
        short mapId = item.getDurability();
		if (item.getItemMeta() != null && item.getItemMeta().getLore() != null
				&& item.getItemMeta().getLore().contains(ArtItem.COPY_KEY)) {
			return new CanvasCopy(item);
		} else {
			return new Canvas(mapId);
		}
    }

	public short getMapId() {
		return this.getDurability();
    }

	public static class CanvasCopy extends Canvas {

        private final String originalName;
		private final Short originalId;

		public CanvasCopy(Map map, String originalName, Short originalId) {
            super(map);
            this.originalName = originalName;
			this.originalId = originalId;
			// Set copy lore
			ItemMeta meta = this.getItemMeta();
			meta.setLore(Arrays.asList(ArtItem.COPY_KEY, originalName, ArtItem.COPY_KEY_ID, originalId.toString()));
			this.setItemMeta(meta);
        }

		public CanvasCopy(ItemStack map) {
			super(map.getDurability());
			ItemMeta meta = map.getItemMeta();
			List<String> lore = meta.getLore();
			if (lore != null && !lore.contains(ArtItem.COPY_KEY)) {
				throw new IllegalArgumentException("The Copied canvas is missing the copy key!");
			}
			this.originalName = lore.get(lore.indexOf(ArtItem.COPY_KEY) + 1);
			if (lore.contains(ArtItem.COPY_KEY_ID)) {
				this.originalId = Short.valueOf(lore.get(lore.indexOf(ArtItem.COPY_KEY_ID) + 1));
			} else {
				this.originalId = null;
			}
		}

		public String getOriginalName() {
			return this.originalName;
		}

		public Short getOriginalId() {
			return this.originalId;
		}
    }
}
