package me.Fupery.ArtMap.Menu.HelpMenu;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Exception.HeadFetchException;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.API.SoundCompat;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;

public class ArtistMenu extends ListMenu implements ChildMenu {

	private final Player viewer;
	private static boolean haveWarnedUser = false;

	public ArtistMenu(Player viewer) {
		super(ChatColor.BLUE + Lang.MenuTitle.MENU_ARTIST.get(), ArtMap.instance().getMenuHandler().MENU.HELP, 0);
		this.viewer = viewer;
	}

	@Override
	public CacheableMenu getParent(Player viewer) {
		return ArtMap.instance().getMenuHandler().MENU.HELP.get(viewer);
	}

	@Override
	protected Future<Button[]> getListItems() {
		FutureTask<Button[]> task = new FutureTask<> (()->{
			UUID[] artists;
			try {
				artists = ArtMap.instance().getArtDatabase().listArtists(this.viewer.getUniqueId());
			} catch (SQLException e) {
				ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
				return new Button[0];
			}
			List<Button> buttons = new LinkedList<>();

			int notCached = artists.length - ArtMap.instance().getHeadsCache().getCacheSize();
			if (notCached > 1 && !haveWarnedUser) {
				this.viewer.sendMessage(MessageFormat.format(
						"ArtMap: {0} artist(s) currently not cached you might see some incorrect heads until they are all loaded.", notCached));
				haveWarnedUser = true;
			}

			//load the player button first
			ArtworkListButton playerButton = null;
			try {
				playerButton = new ArtworkListButton(viewer.getUniqueId(), true);
			} catch (HeadFetchException e) {
				//reload without head data
				try {
					playerButton = new ArtworkListButton(viewer.getUniqueId(), false);
				} catch (HeadFetchException e1) {
					//this one won't fail
				}
			}

			// skip 0 as it is the viewer
			for (int i = 1; i < artists.length; i++) {
				try {
					buttons.add(new ArtworkListButton(artists[i], true));
				} catch (HeadFetchException e) {
					//try again with the fetch set to false as it will fail over and over
					try {
						buttons.add(new ArtworkListButton(artists[i], false));
					} catch (HeadFetchException e1) {
						// this one won't fail 
					}
				}
			}
			// sort the list
			buttons.sort((Button o1, Button o2) -> o1.getItemMeta().getDisplayName().toLowerCase()
					.compareTo(o2.getItemMeta().getDisplayName().toLowerCase()));
			buttons.add(0, playerButton); // add viewer first
			return buttons.toArray(new Button[0]);
		});
		ArtMap.instance().getScheduler().ASYNC.run(task);
		return task;
	}

	public Player getViewer() {
		return Bukkit.getPlayer(this.viewer.getUniqueId());
	}

	private ArtistMenu getMenu() {
		return this;
	}

	public class ArtworkListButton extends Button {

		final UUID artist;
		String artistName;
		//TODO: Fix this when we rearrange the database.  Get the artist name from the database and not try and pull it off the meta
		public ArtworkListButton(UUID artist, boolean loadHeads) throws HeadFetchException {
			super(Material.PLAYER_HEAD);
			this.artist = artist;

			SkullMeta meta = (SkullMeta) getItemMeta();
			this.artistName = artist.toString();
			meta.setDisplayName(artist.toString());
			if(loadHeads) {
				meta = ArtMap.instance().getHeadsCache().getHeadMeta(artist).orElse(meta);
				this.artistName = meta.getDisplayName();
			}
			meta.setLore(Collections.singletonList(HelpMenu.CLICK));
			setItemMeta(meta.clone());
		}

		@Override
		public void onClick(Player player, ClickType clickType) {
			SoundCompat.UI_BUTTON_CLICK.play(player);
			ArtMap.instance().getMenuHandler().openMenu(player,
			        new ArtistArtworksMenu(getMenu(), this.artist, this.artistName, player.hasPermission("artmap.admin"), 0));
		}
	}
}
