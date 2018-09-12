package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Heads.Heads;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;

public class ArtistMenu extends ListMenu implements ChildMenu {

	private final UUID viewer;

	public ArtistMenu(Player viewer) {
		super(ChatColor.BLUE + Lang.MENU_ARTIST.get(), 0);
		this.viewer = viewer.getUniqueId();
	}

	@Override
	public CacheableMenu getParent(Player viewer) {
		return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
	}

	@Override
	protected Button[] getListItems() {
		UUID[] artists = ArtMap.getArtDatabase().listArtists(viewer);
		List<Button> buttons = new LinkedList<Button>();

		// skip 0 as it is the viewer
		for (int i = 1; i < artists.length; i++) {
			buttons.add(new ArtworkListButton(artists[i]));
		}
		// sort the list
		buttons.sort((Button o1, Button o2) -> o1.getItemMeta().getDisplayName().toLowerCase()
				.compareTo(o2.getItemMeta().getDisplayName().toLowerCase()));
		buttons.add(0, new ArtworkListButton(viewer)); // add viewer first
		return buttons.toArray(new Button[0]);
	}

	public Player getViewer() {
		return Bukkit.getPlayer(viewer);
	}

	private ArtistMenu getMenu() {
		return this;
	}

	public class ArtworkListButton extends Button {

		final UUID artist;

		public ArtworkListButton(UUID artist) {
			super(Material.PLAYER_HEAD);
			this.artist = artist;

			SkullMeta meta = (SkullMeta) getItemMeta();
			SkullMeta head = Heads.getHeadMeta(artist);

			if (head != null) {
				meta = head.clone();
			} else {
				meta.setDisplayName(artist.toString());
			}

			meta.setLore(Collections.singletonList(HelpMenu.CLICK));
			setItemMeta(meta);
		}

		@Override
		public void onClick(Player player, ClickType clickType) {
			SoundCompat.UI_BUTTON_CLICK.play(player);
			ArtMap.getMenuHandler().openMenu(player,
					new ArtistArtworksMenu(getMenu(), artist, player.hasPermission("artmap.admin"), 0));
		}
	}
}
