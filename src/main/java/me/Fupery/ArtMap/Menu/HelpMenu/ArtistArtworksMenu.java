package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
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

public class ArtistArtworksMenu extends ListMenu implements ChildMenu {
    private final UUID artist;
    private ArtistMenu parent;
    private boolean adminViewing;

    public ArtistArtworksMenu(ArtistMenu parent, UUID artist, boolean adminViewing, int page) {
        super(processTitle(artist), page);
        this.parent = parent;
        this.adminViewing = adminViewing;
        this.artist = artist;
    }

    private static String processTitle(UUID artist) {
        String name = Bukkit.getOfflinePlayer(artist).getName();
        String title = "§1" + Lang.MENU_ARTWORKS.get();
        String processedName = String.format(title, name);
        if (processedName.length() <= 32) return processedName;
        else return (name.length() <= 30) ? "§1" + name : "§1" + name.substring(0, 29);
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
        if (reason == MenuCloseReason.SPECIAL) return;
        if (ArtMap.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
            ItemStack offHand = viewer.getInventory().getItemInOffHand();
            if (isPreviewItem(offHand)) viewer.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
    }

    @Override
    protected Button[] getListItems() {
		MapArt[] artworks = ArtMap.getArtDatabase().listMapArt(this.artist);
        Button[] buttons;

        if (artworks != null && artworks.length > 0) {
            buttons = new Button[artworks.length];

            for (int i = 0; i < artworks.length; i++) {
                buttons[i] = new PreviewButton(this, artworks[i], adminViewing);
            }

        } else {
            buttons = new Button[0];
        }
        return buttons;
    }

    private class PreviewButton extends Button {

        private final MapArt artwork;
        private final ArtistArtworksMenu artworkMenu;

        private PreviewButton(ArtistArtworksMenu menu, MapArt artwork, boolean adminButton) {
			super(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) artwork.getMapItem().getItemMeta();
			meta.setMapId(artwork.getMapId());
			meta.setLocationName(artwork.getTitle());
            List<String> lore = meta.getLore();
            lore.add(HelpMenu.CLICK);
            if (adminButton) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
            this.artwork = artwork;
            this.artworkMenu = menu;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {

            if (clickType == ClickType.LEFT) {
				ArtMap.getMenuHandler().closeMenu(player, MenuCloseReason.SWITCH);
				ArtMap.getMenuHandler().openMenu(player, new ArtPieceMenu(this.artworkMenu, this.artwork, player));
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
}
