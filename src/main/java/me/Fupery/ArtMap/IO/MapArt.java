package me.Fupery.ArtMap.IO;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;

public class MapArt {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    private final short id;
    private final String title;
    private final UUID artist;
    private final String artistName;
    private final String date;

    public MapArt(short mapIDValue, String title, UUID artist, String artistName, Date date) {
        this(mapIDValue, title, artist, artistName, DATE_FORMAT.format(date));
    }

    public MapArt(short id, String title, UUID artist, String artistName, String date) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.date = date;
        this.artistName = artistName;
    }

    public OfflinePlayer getArtistPlayer() {
        return Bukkit.getOfflinePlayer(artist);
    }

    public boolean isValid() {
        return title != null && title.length() > 2 && title.length() <= 16
                && getArtistPlayer() != null && getArtistPlayer().hasPlayedBefore();
    }

    public boolean equals(MapArt art, boolean ignoreMapID) {
        return (title.equals(art.title) && date.equals(art.date))
                && artist.equals(art.artist)
                && (id == art.id || ignoreMapID);
    }

    @Override
    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder(77, 123);
        builder.append(title);
        builder.append(id);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MapArt && equals(((MapArt) obj), false);
    }

    @Override
    public String toString() {
        return MessageFormat.format("Artwork #{0} created by {1} named {2} on {3}", this.id,this.artist,this.title,this.date);
    }

    public ItemStack getMapItem() {
        return new ArtItem.ArtworkItem(id, title, artistName, date).toItemStack();
    }

    public short getMapId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public UUID getArtist() {
        return artist;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getDate() {
        return date;
    }

    public MapArt setAristName(String name) {
        return new MapArt(this.id, title, this.artist, name, this.date);
    }

    public MapArt updateMapId(short newID) {
        return new MapArt(newID, title, artist, artistName, date);
    }

	public MapArt setTitle(String title) {
		return new MapArt(this.id, title, this.artist, this.artistName, this.date);
	}

    public Map getMap() {
        return new Map(id);
    }
}
