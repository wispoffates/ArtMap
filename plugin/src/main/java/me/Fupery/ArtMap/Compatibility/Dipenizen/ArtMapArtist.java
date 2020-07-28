package me.Fupery.ArtMap.Compatibility.Dipenizen;

import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Fetchable;
import net.aufdemrand.denizencore.objects.dList;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;

public class ArtMapArtist implements dObject {
	protected String prefix = "artmapartist";
	protected UUID artist;

	/////////////////////
	// OBJECT FETCHER
	/////////////////

	public static ArtMapArtist valueOf(String string) {
		return valueOf(string, null);
	}

	@Fetchable("artmapartist")
	public static ArtMapArtist valueOf(String string, TagContext context) {
		if (string == null)
			return null;

		string = string.replace("artmapartist@", "");
		try {
			return new ArtMapArtist(UUID.fromString(string));
		} catch (IllegalArgumentException e) {
			// not a uuid so it should be a name.
		}

		return new ArtMapArtist(Bukkit.getPlayer(string).getUniqueId());
	}

	public static boolean matches(String arg) {
		return arg.startsWith("artmapartist@");
	}

	/////////////////////
	// STATIC CONSTRUCTORS
	/////////////////
	public ArtMapArtist(UUID artist) {
		this.artist = artist;
	}

	/////////////////////
	// dObject Methods
	/////////////////
	@Override
	public boolean equals(Object a) {
		if (a instanceof ArtMapArtist) {
			return ArtMapArtist.class.cast(a).artist.equals(this.artist);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.artist.hashCode();
	}

	@Override
	public String debug() {
		return (this.prefix + "='<A>" + identify() + "<G>' ");
	}

	@Override
	public String getAttribute(Attribute attribute) {
		if (attribute.startsWith("name")) {
			return new Element(Bukkit.getPlayer(this.artist).getName()).getAttribute(attribute);
		}
		if (attribute.startsWith("id")) {
			return new Element(this.artist.toString()).getAttribute(attribute);
		}

		if (attribute.startsWith("artworks")) {
			MapArt[] artworks;
			try {
				artworks = ArtMap.instance().getArtDatabase().listMapArt(this.artist);
			} catch (SQLException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
                return "Error!";
			}
			dList artworksList = new dList();
			for (MapArt art : artworks) {
				artworksList.add(String.valueOf(art.getMapId()));
			}

			return artworksList.getAttribute(attribute.fulfill(1));
		}
		return new Element(identify()).getAttribute(attribute);
	}

	@Override
	public String getObjectType() {
		return "ArtMapArtist";
	}

	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String identify() {
		return "artmapartist@";
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
