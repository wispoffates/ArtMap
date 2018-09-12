package me.Fupery.ArtMap.Easel;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;

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
		if (item == null || item.getType() != Material.FILLED_MAP)
			return null;

		MapMeta meta = (MapMeta) item.getItemMeta();
		short mapId = (short) meta.getMapId();
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
		ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
		MapMeta meta = (MapMeta) mapItem.getItemMeta();
		meta.setMapId(this.mapId);
		mapItem.setItemMeta(meta);
		return mapItem;
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
			super(ItemUtils.getMapID(map));
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
			ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) mapItem.getItemMeta();
			meta.setMapId(this.mapId);
			// Set copy lore
			meta.setLore(Arrays.asList(ArtItem.COPY_KEY, this.original.getTitle()));
			mapItem.setItemMeta(meta);
			return mapItem;
		}

		/**
		 * @return The original map id.
		 */
		public short getOriginalId() {
			return this.original.getMapId();
		}
    }
}
