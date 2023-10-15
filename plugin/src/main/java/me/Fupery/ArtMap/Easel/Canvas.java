package me.Fupery.ArtMap.Easel;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Exception.ArtMapException;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtItem.InProgressArtworkItem;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.api.Config.Lang;

/**
 * Represents a painting canvas..
 *
 */
public class Canvas {

	protected int mapId;
	protected String artist;

	public Canvas(Map map, String artist) {
		this(map.getMapId(), artist);
	}

	protected Canvas(int mapId, String artist) {
		this.mapId = mapId;
		this.artist = artist;
	}

	@NotNull
	/**
	 * Retrieve the canvas of an Artwork.
	 * 
	 * @param item The Artwork to get the canvas of.
	 * @return The canvas if it can be determined or empty if it fails usually mapview being absent.
	 * @throws SQLException Failure getting artwork from the database.
	 * @throws ArtMapException A generic failure parsing the artmap/
	 */
	public static Optional<Canvas> getCanvas(ItemStack item) throws SQLException, ArtMapException {
		if (item == null || item.getType() != Material.FILLED_MAP) {
			throw new ArtMapException("Artmap tried to getCanvas() on something that is not a filled map? :: " + (item==null ? "NULL item" : item.getType()+""));
		}

		//Get map data
		Optional<Integer> optMapId = ItemUtils.getMapID(item);
		if(!optMapId.isPresent()) {
			return Optional.empty();
		}
		int mapId = optMapId.get();
		MapMeta meta = (MapMeta) item.getItemMeta();

		//Is this an unfinished artwork?
		if(ArtItem.isUnfinishedArtwork(item)) {
			//extract artist and id
			return Optional.of(new Canvas(mapId, parseArtist(meta.getLore()).orElse("unknown")));
		}
		
		//Is this a copy artwork?
		if(ArtItem.isCopyArtwork(item)) {
			//Extract id, artist, and original title
			Optional<MapArt> original = ArtMap.instance().getArtDatabase().getArtwork(meta.getDisplayName());
			if(original.isPresent()) {	//There is a chance the original was deleted at which point we act like its an unfished artwork
				return Optional.of(new CanvasCopy(new Map(mapId), original.get()));
			} else {
				//deleted from database try parsing the text
				return Optional.of(new Canvas(new Map(mapId), parseArtist(meta.getLore()).orElse("unknown")));
			}
		}

		//Check if this is an artmap tracked piece. Legacy check.
		//unsaved
		if(ArtMap.instance().getArtDatabase().containsUnsavedArtwork(mapId)){
			return Optional.of(new Canvas(mapId, "unknown"));
		} 
		//previously saved but missing tags
		MapArt art = ArtMap.instance().getArtDatabase().getArtwork(mapId);
		if(art != null) {
			return Optional.of(new CanvasCopy(art.getMap(), art));
		}
		return Optional.empty();
	}

	/**
	 * Parse the artist name out of the lore String.
	 * @param meta List of Strings that is the item meta 
	 * @return The Artist name.
	 */
	public static Optional<String> parseArtist(List<String> meta) {
		String key = Lang.RECIPE_ARTWORK_ARTIST.get().replace("%s", "").trim();
		Optional<String> artistName = meta.stream().filter(s -> s.contains(key)).findFirst();
		if(artistName.isPresent()) {
			return Optional.of(artistName.get().replace(key, "").trim());
		}
		return Optional.empty();
	}

	public ItemStack getEaselItem() {
		return new InProgressArtworkItem(this.mapId, artist).toItemStack();
	}

	public int getMapId() {
		return this.mapId;
	}

	/**
	 * 
	 */
	public static class CanvasCopy extends Canvas {

		private MapArt original;

		public CanvasCopy(Map map, MapArt original) {
			super(map,original.getArtistName());
			this.original = original;
		}

		@Override
		public ItemStack getEaselItem() {
			return new ArtItem.CopyArtworkItem(this.mapId, original.getTitle(), original.getArtistName(), original.getDate()).toItemStack();
		}

		/**
		 * @return The original map id.
		 */
		public int getOriginalId() {
			return this.original.getMapId();
		}
    }
}
