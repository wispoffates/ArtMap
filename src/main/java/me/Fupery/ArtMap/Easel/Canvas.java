package me.Fupery.ArtMap.Easel;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;

/**
 * Represents a painting canvas. Extends ItemStack so that information can be
 * retrieved when it is pulled off the easel.
 *
 */
public class Canvas {

	protected short mapId;

    public Canvas(Map map) {
		this(map.getMapId());
    }

	protected Canvas(short mapId) {
		this.mapId = mapId;
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

	public ItemStack getDroppedItem() {
		return ArtMaterial.CANVAS.getItem();
	}

	public ItemStack getEaselItem() {
		return new ItemStack(Material.MAP, 1, this.mapId);
	}

	public short getMapId() {
		return this.mapId;
    }

	public static class CanvasCopy extends Canvas {

		private MapArt original;

		public CanvasCopy(Map map, MapArt orginal) {
			super(map);
			this.original = orginal;
		}

		public CanvasCopy(ItemStack map) {
			super(map.getDurability());
			ItemMeta meta = map.getItemMeta();
			List<String> lore = meta.getLore();
			if (lore != null && !lore.contains(ArtItem.COPY_KEY)) {
				throw new IllegalArgumentException("The Copied canvas is missing the copy key!");
			}
			String originalName = lore.get(lore.indexOf(ArtItem.COPY_KEY) + 1);
			this.original = ArtMap.getArtDatabase().getArtwork(originalName);
		}

		@Override
		public ItemStack getDroppedItem() {
			return this.original.getMapItem();
		}

		@Override
		public ItemStack getEaselItem() {
			ItemStack item = new ItemStack(Material.MAP, 1, this.mapId);
			// Set copy lore
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList(ArtItem.COPY_KEY, this.original.getTitle()));
			item.setItemMeta(meta);
			return item;
		}

		/**
		 * @return The original map id.
		 */
		public short getOriginalId() {
			return this.original.getMapId();
		}
    }
}
