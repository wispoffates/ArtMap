package me.Fupery.ArtMap.Easel;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Exception.ArtMapException;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;
import me.Fupery.ArtMap.api.Easel.ClickType;
import net.md_5.bungee.api.ChatColor;

public final class EaselEvent {
	private final Easel easel;
	private final ClickType click;
	private final Player player;

	public EaselEvent(Easel easel, ClickType click, Player player) {
		this.easel = easel;
		this.click = click;
		this.player = player;
	}

	public void callEvent() {
		if (!player.hasPermission("artmap.artist")) {
			Lang.NO_PERM.send(player);
			return;
		}
		if (easel.isBeingUsed()) {
			Lang.ActionBar.ELSE_USING.send(player);
			easel.playEffect(EaselEffect.USE_DENIED);
			return;
		}
		if (ArtMap.instance().getPreviewManager().endPreview(player))
			return;

		switch (click) {
			case LEFT_CLICK:
				Lang.ActionBar.EASEL_HELP.send(player);
				return;
			case RIGHT_CLICK:
				if (easel.getItem().getType() == Material.FILLED_MAP) {
					// If the easel has a canvas, player rides the easel
					try {
						ArtMap.instance().getArtistHandler().addPlayer(player, easel,
								new Map(ItemUtils.getMapID(easel.getItem())),
								EaselPart.getYawOffset(easel.getFacing()));
					} catch (Exception e) {
						ArtMap.instance().getLogger().log(Level.SEVERE, "Failure having player mount the Easel!", e);
					}
					return;
				} else if (easel.getItem().getType() != Material.AIR) {
					// remove items that were added while instance is unloaded etc.
					try {
						easel.removeItem();
					} catch (SQLException | ArtMapException e) {
						ArtMap.instance().getLogger().log(Level.SEVERE, "Unexpected item on Easel!", e);
					}
					return;
				}
				ItemStack itemInHand = player.getInventory().getItemInMainHand();
				ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

				if (material == ArtMaterial.CANVAS) {
					// Mount a canvas on the easel
					Map map = null;
					try {
						map = ArtMap.instance().getArtDatabase().createMap();
					} catch (NoSuchFieldException | IllegalAccessException e) {
						player.sendMessage(
							ChatColor.RED + " Severe Error.  Pleae contact a server Admin! " + ChatColor.RESET);
						ArtMap.instance().getLogger().log(Level.SEVERE, "Error creating map!", e);
					}
				if (map == null) {
					player.sendMessage(
							ChatColor.RED + " Severe Error.  Pleae contact a server Admin! " + ChatColor.RESET);
					return;
				}
				map.update(player);
				mountCanvas(itemInHand, new Canvas(map,player.getName()));
			} else if (ArtItem.isArtwork(itemInHand)) {
				// Edit an artwork on the easel
				ArtMap.instance().getScheduler().ASYNC.run(() -> {
					MapArt art;
					int id;
					boolean unsaved;
					try {
						id = ItemUtils.getMapID(itemInHand);
						art = ArtMap.instance().getArtDatabase().getArtwork(id);
						unsaved = ArtMap.instance().getArtDatabase().containsUnsavedArtwork(id);
					} catch(Exception e) {
						player.sendMessage("Error placing art on the Easel!");
						ArtMap.instance().getLogger().log(Level.SEVERE, "Error placing art on easel for edit!",e );
						return;
					}
					ArtMap.instance().getScheduler().SYNC.run(() -> {
						if (art != null) {
							if (!player.getUniqueId().equals(art.getArtistPlayer().getUniqueId()) && !player.hasPermission("artmap.admin")) {
								Lang.ActionBar.NO_EDIT_PERM.send(player);
								easel.playEffect(EaselEffect.USE_DENIED);
								return;
							}
							try {
								Canvas canvas = new Canvas.CanvasCopy(art.getMap().cloneMap(), art);
								mountCanvas(itemInHand, canvas);
							} catch (Exception e) {
								player.sendMessage("Error placing art on the Easel!");
								ArtMap.instance().getLogger().log(Level.SEVERE, "Error placing art on easel for edit!",e );
							}
						} else if ( unsaved ) {
							Canvas canvas = new Canvas(id, player.getName());
							mountCanvas(itemInHand, canvas);
						} else {
							Lang.ActionBar.NEED_CANVAS.send(player);
							easel.playEffect(EaselEffect.USE_DENIED);
						}
					});
				});
			} else {
				Lang.ActionBar.NEED_CANVAS.send(player);
				easel.playEffect(EaselEffect.USE_DENIED);
			}
			return;

		case SHIFT_RIGHT_CLICK:
			/*
			if (easel.hasItem()) {
				this.player.sendMessage(Lang.SAVE_ARTWORK.get());
				this.player.sendMessage(Lang.SAVE_ARTWORK_2.get());
				return;
			}*/
			easel.breakEasel();
		}
	}

	private void mountCanvas(ItemStack itemInHand, Canvas canvas) {
		easel.mountCanvas(canvas);
		ItemStack removed = itemInHand.clone();
		removed.setAmount(1);
		player.getInventory().removeItem(removed);
	}
}
