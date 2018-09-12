package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.ListMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.Utils.VersionHandler;
import net.wesjd.anvilgui.AnvilGUI;

public class ArtPieceMenu extends ListMenu implements ChildMenu {
	private ArtistArtworksMenu parent;
	private boolean adminViewing;
	private MapArt artwork;

	public ArtPieceMenu(ArtistArtworksMenu parent, MapArt artwork, boolean adminViewing) {
		super(artwork.getTitle(), 0);
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
	public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
		if (reason == MenuCloseReason.SPECIAL)
			return;
		if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
			ItemStack offHand = viewer.getInventory().getItemInOffHand();
			if (isPreviewItem(offHand))
				viewer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
		}
	}

	@Override
	protected Button[] getListItems() {
		List<Button> buttons = new ArrayList<>();
		buttons.add(new PreviewButton(this, this.artwork, adminViewing));
		buttons.add(new DeleteButton(this.parent, this.artwork, adminViewing));
		buttons.add(new RenameButton(this.parent, this.artwork, adminViewing));
		return buttons.toArray(new Button[0]);
	}

	private class PreviewButton extends Button {

		private final MapArt artwork;
		private final ArtPieceMenu artworkMenu;

		private PreviewButton(ArtPieceMenu menu, MapArt artwork, boolean adminButton) {
			super(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) artwork.getMapItem().getItemMeta();
			meta.setMapId(artwork.getMapId());
			List<String> lore = meta.getLore();
			lore.add(HelpMenu.CLICK);
			if (adminButton)
				lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
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
					SoundCompat.BLOCK_CLOTH_FALL.play(player);
					ItemStack preview = artwork.getMapItem();
					MapMeta meta = (MapMeta) preview.getItemMeta();
					meta.setMapId(artwork.getMapId());
					List<String> lore = getItemMeta().getLore();
					lore.set(0, ArtItem.PREVIEW_KEY);
					meta.setLore(lore);
					preview.setItemMeta(meta);
					ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.SPECIAL);
					player.getInventory().setItemInOffHand(preview);
					ArtMap.getMenuHandler().openMenu(player, this.artworkMenu);
				} else {
					Lang.EMPTY_HAND_PREVIEW.send(player);
				}
			} else if (clickType == ClickType.RIGHT) {
				if (player.hasPermission("artmap.admin")) {
					SoundCompat.BLOCK_CLOTH_FALL.play(player);
					ArtMap.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, artwork.getMapItem()));
				} else if (adminViewing) {
					Lang.NO_PERM.send(player);
				}
			}
		}
	}

	private class DeleteButton extends Button {

		private final MapArt artwork;
		private final ArtistArtworksMenu parent;

		private DeleteButton(ArtistArtworksMenu parent, MapArt artwork, boolean adminButton) {
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
				ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

				ArtMap.getScheduler().SYNC.run(() -> {
					ArtMap.getMenuHandler().openMenu(player,
							new DeleteConfirmationMenu(this.parent, this.artwork, false));
				});
			}
		}
	}

	private class RenameButton extends Button {

		private final MapArt artwork;
		private final ArtistArtworksMenu artworkMenu;

		private RenameButton(ArtistArtworksMenu menu, MapArt artwork, boolean adminButton) {
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
				ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.DONE);

				if (this.artwork.getArtist().equals(player.getUniqueId()) || player.hasPermission("artmap.admin")) {
					new AnvilGUI(ArtMap.instance(), player, Lang.TITLE_QUESTION.get(), (p, reply) -> {
						ArtMap.getScheduler().SYNC.run(() -> {
							if (ArtMap.getArtDatabase().renameArtwork(this.artwork, reply)) {
								player.sendMessage(String.format(Lang.RENAMED.get(), this.artwork.getTitle()));
							} else {
								player.sendMessage(String.format(Lang.MAP_NOT_FOUND.get(), this.artwork.getTitle()));
							}

							ArtMap.getMenuHandler().openMenu(player, this.artworkMenu.getParent(player));
						});
						return null;
					});
				} else {
					player.sendMessage(Lang.NO_PERM.get() + " " + this.artwork.getArtist().equals(player.getUniqueId())
							+ " " + player.hasPermission("artmap.admin"));
					return;
				}
			}
		}
	}
}
