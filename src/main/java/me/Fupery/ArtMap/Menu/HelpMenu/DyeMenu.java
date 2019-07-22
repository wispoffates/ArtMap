package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

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
		buttons.add(new StaticButton(ArtMap.getBukkitVersion().getVersion().getSign(), Lang.Array.INFO_DYES.get()));
		// buttons[53] = new CloseButton();

		for (ArtDye dye : dyes) {
			buttons.add(new DyeButton(dye));
        }
		return buttons.toArray(new Button[0]);
	}

	private static class DyeButton extends Button {

		private ArtDye dye;

		public DyeButton(ArtDye dye) {
			super(dye.toItem());
			this.dye = dye;
		}

		@Override
		public void onClick(Player player, ClickType clickType) {
			if(!player.hasPermission("artmap.admin")) {
				return;
			}
			if(clickType.isRightClick()) {
				player.getInventory().addItem(dye.toItem());
			} else {
				player.getOpenInventory().setCursor(dye.toItem());
			}
		}
		
	}
}
