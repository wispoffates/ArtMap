package me.Fupery.ArtMap.Compatibility.Dipenizen;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Fetchable;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;

public class ArtMapArtists implements dObject {

	protected String prefix = "artmapartists";
	protected UUID[] artists;

	/////////////////////
	// OBJECT FETCHER
	/////////////////

	public static ArtMapArtists valueOf(String string) {
		return valueOf(string, null);
	}

	@Fetchable("artmapartists")
	public static ArtMapArtists valueOf(String string, TagContext context) {
		if (string == null)
			return null;

		//string = string.replace("artmapartists@", "");
		try {
			return new ArtMapArtists(ArtMap.instance().getArtDatabase().listArtists());
		} catch (SQLException e) {
			ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
			return null;
		}
	}
	
	public static boolean matches(String arg) {
		return arg.startsWith("artmapartists@");
	}

	/////////////////////
	// STATIC CONSTRUCTORS
	/////////////////
	public ArtMapArtists(UUID[] artists) {
		this.artists = Arrays.copyOf(artists, artists.length);
	}

	/////////////////////
	// dObject Methods
	/////////////////
	@Override
	public boolean equals(Object a) {
		if (a instanceof ArtMapArtists) {
			return Arrays.equals(ArtMapArtists.class.cast(a).artists,this.artists);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(this.artists);
	}

	@Override
	public String debug() {
		return (this.prefix + "='<A>" + identify() + "<G>' ");
	}

	@Override
	public String getAttribute(Attribute attribute) {
		if (attribute.startsWith("artistsbyname")) {
			dList artistList = new dList();
			for (UUID id : this.artists) {
				artistList.add(Bukkit.getPlayer(id).getName());
			}
			return artistList.getAttribute(attribute.fulfill(1));
		} else if (attribute.startsWith("artists")) {
			dList artistList = new dList();
			for (UUID id : this.artists)
				artistList.add(id.toString());

			return artistList.getAttribute(attribute.fulfill(1));
		}
		return new Element(identify()).getAttribute(attribute);
	}

	@Override
	public String getObjectType() {
		return "ArtMapArtists";
	}

	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String identify() {
		return "artmapartists@";
	}

	@Override
	public String identifySimple() {
		return identify();
	}

	@Override
	public boolean isUnique() {
		return true;
	}

	@Override
	public dObject setPrefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	

}
