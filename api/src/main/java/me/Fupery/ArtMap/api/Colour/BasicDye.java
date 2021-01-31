package me.Fupery.ArtMap.api.Colour;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import me.Fupery.ArtMap.api.Painting.Pixel;

public class BasicDye extends ArtDye {

    private final byte colour;

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
	public BasicDye(String localizedName, String englishName, int colour, ChatColor chatColor, Material material) {
		super(localizedName, englishName, chatColor, material);
        this.colour = (byte) colour;

    }

	/**
	 * Return the byte color representation of this dye. ColorID * 4 + 1 Multiply by
	 * 4 as each Minecraft color space is 4 wide. Add 1 to place it on the second
	 * lightest color in the space as Minecraft color space is 3,2,1,4 brightness
	 * for some reason.
	 * 
	 * @return The Minecraft byte color representation.
	 */
    public byte getColour() {
        return (byte) ((this.colour*4)+1);
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColour(getColour());
    }

    @Override
    public byte getDyeColour(byte currentPixelColour) {
        return getColour();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicDye)) return false;
        BasicDye dye = (BasicDye) obj;
        return dye.colour == colour;
    }

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
