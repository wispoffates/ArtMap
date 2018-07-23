package me.Fupery.ArtMap.Compatability.Dipenizen;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;
import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizencore.objects.Element;
import net.aufdemrand.denizencore.objects.Fetchable;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.tags.Attribute;
import net.aufdemrand.denizencore.tags.TagContext;

public class ArtMapArt implements dObject {
	protected String prefix = "artmapart";
	protected short artId;

	/////////////////////
	// OBJECT FETCHER
	/////////////////

	public static ArtMapArt valueOf(String string) {
		return valueOf(string, null);
	}

	@Fetchable("artmapart")
	public static ArtMapArt valueOf(String string, TagContext context) {
		if (string == null)
			return null;

		string = string.replace("artmapart@", "");
		try {
			return new ArtMapArt(Short.parseShort(string));
		} catch (IllegalArgumentException e) {
			// not a uuid so it should be a name.
		}

		return null;
	}

	public static boolean matches(String arg) {
		return arg.startsWith("artmapart@");
	}

	/////////////////////
	// STATIC CONSTRUCTORS
	/////////////////
	public ArtMapArt(Short artId) {
		this.artId = artId;
		ArtMap.getArtDatabase().getArtwork(this.artId);
	}

	/////////////////////
	// dObject Methods
	/////////////////
	@Override
	public boolean equals(Object a) {
		if (a instanceof ArtMapArt) {
			return ArtMapArt.class.cast(a).artId == this.artId;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.artId;
	}

	@Override
	public String debug() {
		return (this.prefix + "='<A>" + identify() + "<G>' ");
	}

	@Override
	public String getAttribute(Attribute attribute) {
		MapArt art = ArtMap.getArtDatabase().getArtwork(this.artId);
		if (attribute.startsWith("name")) {
			return new Element(art.getTitle()).getAttribute(attribute);
		}
		if (attribute.startsWith("id")) {
			return new Element(art.getMapId()).getAttribute(attribute);
		}
		if (attribute.startsWith("date")) {
			return new Element(art.getDate()).getAttribute(attribute);
		}
		if (attribute.startsWith("id")) {
			return new Element(art.getMapId()).getAttribute(attribute);
		}
		if (attribute.startsWith("item")) {
			dItem artwork = new dItem(art.getMapItem());
			return artwork.getAttribute(attribute.fulfill(1));
		}
		return new Element(identify()).getAttribute(attribute);
	}

	@Override
	public String getObjectType() {
		return "artmapart";
	}

	@Override
	public String getPrefix() {
		return this.prefix;
	}

	@Override
	public String identify() {
		return "artmapart@";
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
