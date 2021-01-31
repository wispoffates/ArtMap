package me.Fupery.ArtMap.Menu.HelpMenu;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.API.SoundCompat;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Preview.ArtPreview;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ItemUtils;
import net.wesjd.anvilgui.AnvilGUI;

public class ArtPieceMenu extends ListMenu implements ChildMenu {
	private ArtistArtworksMenu parent;
	private Player viewer;
	private MapArt artwork;

	public ArtPieceMenu(ArtistArtworksMenu parent, MapArt artwork, Player viewer) {
		super(String.format(Lang.MenuTitle.MENU_ARTWORK.get(), artwork.getTitle()),0);
		this.parent = parent;
		this.artwork = artwork;
		this.viewer = viewer;
	}

	public static boolean isPreviewItem(ItemStack item) {
		return item != null && item.getType() == Material.FILLED_MAP && item.hasItemMeta()
				&& item.getItemMeta().hasLore() && item.getItemMeta().getLore().get(0).equals(ArtItem.PREVIEW_KEY);
	}

	@Override
	public CacheableMenu getParent(Player viewer) {
		return parent;
	}

	@Override
	public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
		if (reason == MenuCloseReason.SPECIAL)
			return;
		ItemStack offHand = viewer.getInventory().getItemInOffHand();
		if (isPreviewItem(offHand))
			viewer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));	
	}

	@Override
	protected Future<Button[]> getListItems() {
		FutureTask<Button[]> task = new FutureTask<> (()->{
			List<Button> buttons = new ArrayList<>();
			buttons.add(new PreviewButton(this, this.artwork, viewer));
			if (this.viewer.hasPermission("artmap.admin") || this.artwork.getArtist().equals(this.viewer.getUniqueId())) {
				buttons.add(new DeleteButton(this.parent, this.artwork));
				buttons.add(new RenameButton(this.parent, this.artwork));
			}
			return buttons.toArray(new Button[0]);
		});
		ArtMap.instance().getScheduler().SYNC.run(task);
		return task;
	}

	private static class PreviewButton extends Button {

		private final MapArt artwork;
		private final ArtPieceMenu artworkMenu;

		private PreviewButton(ArtPieceMenu menu, MapArt artwork, Player player) {
			super(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) artwork.getMapItem().getItemMeta();
			meta.setMapView(ArtMap.getMap(artwork.getMapId()));
			List<String> lore = meta.getLore();
			lore.add(HelpMenu.CLICK);
			if (player.hasPermission("artmap.admin")) {
				lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
			} else if (artwork.getArtistPlayer().equals(player)) {
				lore.add(ChatColor.GOLD + Lang.RECIPE_PLAYER_MAP_COPY.get());
			}
			meta.setLore(lore);
			setItemMeta(meta);
			this.artwork = artwork;
			this.artworkMenu = menu;
		}

		@Override
		public void onClick(Player player, ClickType clickType) {

			if (clickType == ClickType.LEFT) {
				ItemStack offHand = player.getInventory().getItemInOffHand();
				if (offHand.getType() == Material.AIR || isPreviewItem(offHand)) {
					ArtMap.instance().getMenuHandler().closeMenu(player, MenuCloseReason.SPECIAL);
					ArtMap.instance().getPreviewManager().startPreview(player, new ArtPreview(artwork));
					ArtMap.instance().getMenuHandler().openMenu(player, this.artworkMenu);
				} else {
					Lang.EMPTY_HAND_PREVIEW.send(player);
				}
			} else if (clickType == ClickType.RIGHT) {
				if (player.hasPermission("artmap.admin")) {
					SoundCompat.BLOCK_CLOTH_FALL.play(player);
					ArtMap.instance().getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, artwork.getMapItem()));
				} else if (artwork.getArtistPlayer().equals(player)) {
					if (player.getInventory().contains(Material.MAP)) {
						// remove a map from the player
						HashMap<Integer, ? extends ItemStack> maps = player.getInventory().all(Material.MAP);
						ItemStack map = maps.entrySet().iterator().next().getValue();
						map.setAmount(map.getAmount() - 1);
						ArtMap.instance().getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, artwork.getMapItem()));
					} else {
						player.sendMessage(Lang.RECIPE_PLAYER_MAP_COPY_MISSING.get());
					}
				} else {
					Lang.NO_PERM.send(player);
				}
			}
		}
	}

	private static class DeleteButton extends Button {

		private final MapArt artwork;
		private final ArtistArtworksMenu parent;

		private DeleteButton(ArtistArtworksMenu parent, MapArt artwork) {
			super(Material.REDSTONE);
			ItemMeta meta = new ItemStack(Material.REDSTONE).getItemMeta();
			meta.setDisplayName(HelpMenu.DELETE_NAME);
			List<String> lore = new ArrayList<>();
			lore.add(HelpMenu.DELETE_TEXT);
			meta.setLore(lore);
			setItemMeta(meta);
			this.artwork = artwork;
			this.parent = parent;
		}

		@Override
		public void onClick(Player player, ClickType clickType) {

			if (clickType == ClickType.LEFT) {
				ArtMap.instance().getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

				ArtMap.instance().getScheduler().SYNC.run(() -> {
					ArtMap.instance().getMenuHandler().openMenu(player,
							new DeleteConfirmationMenu(this.parent, this.artwork, false));
				});
			}
		}
	}

	private static class RenameButton extends Button {

		private final MapArt artwork;
		private final ArtistArtworksMenu artworkMenu;

		private RenameButton(ArtistArtworksMenu menu, MapArt artwork) {
			super(Material.WRITABLE_BOOK);
			ItemMeta meta = new ItemStack(Material.REDSTONE).getItemMeta();
			meta.setDisplayName(HelpMenu.RENAME_NAME);
			List<String> lore = new ArrayList<>();
			lore.add(HelpMenu.RENAME_TEXT);
			meta.setLore(lore);
			setItemMeta(meta);
			this.artwork = artwork;
			this.artworkMenu = menu;
		}

		@Override
		public void onClick(Player player, ClickType clickType) {

			if (clickType == ClickType.LEFT) {
				ArtMap.instance().getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

				if (this.artwork.getArtist().equals(player.getUniqueId()) || player.hasPermission("artmap.admin")) {
					AnvilGUI.Builder gui = new AnvilGUI.Builder();
					gui.plugin(ArtMap.instance()).text(Lang.TITLE_QUESTION.get()).onComplete((p, reply) -> {
						ArtMap.instance().getScheduler().SYNC.run(() -> {
							try {
								ArtMap.instance().getArtDatabase().renameArtwork(this.artwork, reply);
								player.sendMessage(String.format(Lang.RENAMED.get(), this.artwork.getTitle()));
								ArtMap.instance().getMenuHandler().openMenu(player, this.artworkMenu.getParent(player));
							} catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
								ArtMap.instance().getLogger().log(Level.SEVERE, "Rename Artwork Failure!", e);
								player.sendMessage("Error Renaming Artwork check logs.");
            					return; 
							}
						});
						return null;
					});
					gui.open(player);
				} else {
					player.sendMessage(Lang.NO_PERM.get() + " " + this.artwork.getArtist().equals(player.getUniqueId())
							+ " " + player.hasPermission("artmap.admin"));
					return;
				}
			}
		}
	}
}
