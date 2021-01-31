package me.Fupery.ArtMap.Menu.Button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Menu.API.SoundCompat;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;

public class CloseButton extends Button {

    public CloseButton() {
        super(Material.BARRIER, Lang.Array.HELP_CLOSE.get());
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, 3);
        ArtMap.instance().getMenuHandler().closeMenu(player, MenuCloseReason.BACK);
    }
}
