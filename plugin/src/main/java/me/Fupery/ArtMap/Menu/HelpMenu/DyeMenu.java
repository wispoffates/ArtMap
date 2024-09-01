package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.DyeType;

public class DyeMenu extends ListMenu {

    public DyeMenu() {
		super(Lang.MenuTitle.MENU_DYES.get(), ArtMap.instance().getMenuHandler().MENU.HELP, 0);
    }

    @Override
	protected Future<List<Button>> getListItems() {
		List<Button> buttons = new ArrayList<>();
        ArtDye[] dyes = ArtMap.instance().getDyePalette().getDyes(DyeType.DYE);
		buttons.add(new StaticButton(ArtMap.instance().getBukkitVersion().getVersion().getSign(), Lang.Array.INFO_DYES.get()));
		// buttons[53] = new CloseButton();

		for (ArtDye dye : dyes) {
			buttons.add(new DyeButton(dye));
        }
		return CompletableFuture.completedFuture(buttons);
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
				ArtMap.instance().getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, dye.toItem()));
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((dye == null) ? 0 : dye.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			DyeButton other = (DyeButton) obj;
			if (dye == null) {
				if (other.dye != null)
					return false;
			} else if (!dye.equals(other.dye))
				return false;
			return true;
		}

		
		
	}
}
