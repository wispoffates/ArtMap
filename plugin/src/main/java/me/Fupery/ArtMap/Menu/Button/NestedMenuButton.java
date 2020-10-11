package me.Fupery.ArtMap.Menu.Button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.SoundCompat;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;

public class NestedMenuButton extends Button {

    private final CacheableMenu linkedMenu;

    public NestedMenuButton(CacheableMenu linkedMenu, Material material) {
        super(material);
        this.linkedMenu = linkedMenu;
    }

    public NestedMenuButton(CacheableMenu linkedMenu, Material material, String displayName, String... lore) {
        super(material, displayName, lore);
        this.linkedMenu = linkedMenu;
    }

    public NestedMenuButton(CacheableMenu linkedMenu, Material material, String... text) {
        super(material, text);
        this.linkedMenu = linkedMenu;
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        SoundCompat.UI_BUTTON_CLICK.play(player);
        ArtMap.instance().getMenuHandler().openMenu(player, linkedMenu);
    }
}
