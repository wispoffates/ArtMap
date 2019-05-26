package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Colour.ArtDye;
import me.Fupery.ArtMap.Colour.DyeType;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.StaticButton;

public class DyeMenu extends ListMenu {

    public DyeMenu() {
		super("Dyes for Painting", ArtMap.getMenuHandler().MENU.HELP, 0);
    }

    @Override
	protected Button[] getListItems() {
		List<Button> buttons = new ArrayList<>();
        ArtDye[] dyes = ArtMap.getDyePalette().getDyes(DyeType.DYE);
		buttons.add(new StaticButton(Material.OAK_SIGN, Lang.Array.INFO_DYES.get()));
		// buttons[53] = new CloseButton();

		for (ArtDye dye : dyes) {
			buttons.add(new StaticButton(dye.toItem()));
        }
		return buttons.toArray(new Button[0]);
    }
}
