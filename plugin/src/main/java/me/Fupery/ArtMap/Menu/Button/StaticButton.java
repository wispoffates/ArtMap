package me.Fupery.ArtMap.Menu.Button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class StaticButton extends Button {

    public StaticButton(Material material, String displayName, String... lore) {
        super(material, displayName, lore);
    }

    public StaticButton(Material material, String... text) {
        super(material, text);
    }

    public StaticButton(Material material) {
        super(material);
    }

    public StaticButton(ItemStack itemStack) {
		super(itemStack.getType());
        setAmount(itemStack.getAmount());
        setItemMeta(itemStack.getItemMeta().clone());
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        //do nothing
    }
}
