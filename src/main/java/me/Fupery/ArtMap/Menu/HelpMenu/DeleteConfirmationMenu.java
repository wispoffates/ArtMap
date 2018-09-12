package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Recipe.ArtItem;

public class DeleteConfirmationMenu extends ListMenu implements ChildMenu {
	private ArtistArtworksMenu parent;
	private boolean adminViewing;
	private MapArt artwork;

	public DeleteConfirmationMenu(ArtistArtworksMenu parent, MapArt artwork, boolean adminViewing) {
		super(HelpMenu.DELETE_NAME, 0);
		this.parent = parent;
		this.adminViewing = adminViewing;
		this.artwork = artwork;
	}

	public static boolean isPreviewItem(ItemStack item) {
		return item != null && item.getType() == Material.FILLED_MAP && item.hasItemMeta() && item.getItemMeta().hasLore()
				&& item.getItemMeta().getLore().get(0).equals(ArtItem.PREVIEW_KEY);
	}

	@Override
	public CacheableMenu getParent(Player viewer) {
		return parent;
	}

	@Override
	protected Button[] getListItems() {
		List<Button> buttons = new ArrayList<>();
		buttons.add(new AcceptButton(this.parent, this.artwork, adminViewing));
		return buttons.toArray(new Button[0]);
	}

	private class AcceptButton extends Button {

		private final MapArt artwork;
		private final ArtistArtworksMenu artworkMenu;

		private AcceptButton(ArtistArtworksMenu menu, MapArt artwork, boolean adminButton) {
			super(Material.REDSTONE);
			ItemMeta meta = new ItemStack(Material.REDSTONE).getItemMeta();
			meta.setDisplayName(HelpMenu.ACCEPT_NAME);
			List<String> lore = new ArrayList<>();
			lore.add(HelpMenu.ACCEPT_TEXT);
			meta.setLore(lore);
			setItemMeta(meta);
			this.artwork = artwork;
			this.artworkMenu = menu;
		}

		@Override
		public void onClick(Player player, ClickType clickType) {

			if (clickType == ClickType.LEFT) {
				ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

				ArtMap.getScheduler().SYNC.run(() -> {
					if (this.artwork.getArtist().equals(player.getUniqueId()) || player.hasPermission("artmap.admin")) {
						if (ArtMap.getArtDatabase().deleteArtwork(this.artwork)) {
							player.sendMessage(String.format(Lang.DELETED.get(), this.artwork.getTitle()));
						} else {
							player.sendMessage(String.format(Lang.MAP_NOT_FOUND.get(), this.artwork.getTitle()));
						}
					} else {
						player.sendMessage(Lang.NO_PERM.get());
						return;
					}
					ArtMap.getMenuHandler().openMenu(player, this.artworkMenu);
				});
			}
		}
	}
}
