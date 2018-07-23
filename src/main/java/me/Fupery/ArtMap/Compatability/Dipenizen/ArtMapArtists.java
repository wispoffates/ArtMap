package me.Fupery.ArtMap.Compatability.Dipenizen;

import java.util.UUID;

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
		return valueOf(string,null);
	}
	
	@Fetchable("artmapartists")
	public static ArtMapArtists valueOf(String string, TagContext context) {
		if(string == null)
			return null;
		
		string = string.replace("artmapartists@", "");
		return new ArtMapArtists(ArtMap.getArtDatabase().listArtists());
	}
	
	public static boolean matches(String arg) {
		return arg.startsWith("artmapartists@");
	}

	/////////////////////
	// STATIC CONSTRUCTORS
	/////////////////
	public ArtMapArtists(UUID[] artists) {
		this.artists = artists;
	}

	/////////////////////
	// dObject Methods
	/////////////////
	@Override
	public boolean equals(Object a) {
		if (a instanceof ArtMapArtists) {
			return ArtMapArtists.class.cast(a).artists.equals(this.artists);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.artists.hashCode();
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
