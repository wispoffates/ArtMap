package me.Fupery.ArtMap.api.Colour;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.Fupery.ArtMap.api.Painting.Pixel;

public class ShadingDye extends ArtDye {

    private final boolean darken;

    public ShadingDye(String localizedName, String englishName, boolean darken, ChatColor chatColour, Material material) {
        super(localizedName, englishName, chatColour, material);
        this.darken = darken;
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColour(getDyeColour(pixel.getColour()));
    }

    @Override
	public byte getDyeColour(final byte currentPixelColour) {
		int current = (currentPixelColour & 0xFF); // convert back to int representation from signed byte
		int shade = current % 4; // 0-3
		int shift;

        if (darken) {
            if (shade > 0 && shade < 3) {
                shift = -1;
            } else if (shade == 0) {
                shift = 3;
            } else {
                return currentPixelColour;
            }
        } else {
            if (shade < 2 && shade >= 0) {
                shift = 1;
            } else if (shade == 3) {
                shift = -3;
            } else {
                return currentPixelColour;
            }
        }
		/*
		 * ArtMap.instance().getLogger().info("Current color=" + current +
		 * " Current shade=" + shade + " Shift=" + shift + "New Color=" + (byte)
		 * (current + shift));
		 */
		return (byte) (current + shift);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShadingDye)) return false;
        ShadingDye dye = (ShadingDye) obj;
        return dye.darken == darken;
    }

	@Override
	public int hashCode() {
		return super.hashCode();
	}

    @Override
    public byte getColour() {
        //does nothing 
        return 0;
    }
}
