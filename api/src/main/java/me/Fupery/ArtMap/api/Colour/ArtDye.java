package me.Fupery.ArtMap.api.Colour;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.api.Painting.Pixel;


public abstract class ArtDye {
    private final String localizedName;
    private final String englishName;   //Minecraft servers do not like localized names as recipe keys
    private final ChatColor chatColour;
    private Material material;

    /**
     * Durability value of -1 indicates that items of any durability will be accepted
     */
	protected ArtDye(String localizedName, String englishName, ChatColor chatColor, Material material) {
        this.localizedName = localizedName;
        this.englishName = englishName;
        this.chatColour = chatColor;
        this.material = material;
    }

    public abstract void apply(Pixel pixel);

    public abstract byte getDyeColour(byte currentPixelColour);

    public String name() {
        return chatColour + localizedName;
    }

    public String rawName() {
        return localizedName;
    }

    public String englishName() {
        return englishName.toUpperCase();
    }


    public ChatColor getDisplayColour() {
        return chatColour;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack toItem() {
		ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chatColour + localizedName);
        item.setItemMeta(meta);
        return item;
    }

    public abstract byte getColour();
}
