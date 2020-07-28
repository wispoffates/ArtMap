package me.Fupery.ArtMap.Menu.Button;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Button extends ItemStack {
    public Button(Material material) {
        super(material);
    }

    public Button(Material material, String displayName, String... lore) {
		super(material);
        ItemMeta meta = getItemMeta();
        if (displayName != null) meta.setDisplayName(displayName);
        if (lore != null && lore.length > 0) meta.setLore(Arrays.asList(lore));
        setItemMeta(meta);
    }

	public Button(Material material, String... text) {
		super(material);
        ItemMeta meta = getItemMeta();
        if (text != null && text.length > 0) {
            meta.setDisplayName(text[0]);
            if (text.length > 1) {
                String[] lore = new String[text.length - 1];
                System.arraycopy(text, 1, lore, 0, text.length - 1);
                meta.setLore(Arrays.asList(lore));
            }
        }
        setItemMeta(meta);
    }

	public Button(Material material, String text) {
		super(material);
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(text);
		setItemMeta(meta);
    }
    
    public Button(ItemStack item) {
        super(item.getType());
        setItemMeta(item.getItemMeta());
    }
    
    public abstract void onClick(Player player, ClickType clickType);

}
