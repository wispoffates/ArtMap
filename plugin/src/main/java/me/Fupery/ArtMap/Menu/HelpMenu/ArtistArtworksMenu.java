package me.Fupery.ArtMap.Menu.HelpMenu;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
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
import me.Fupery.ArtMap.Recipe.ArtItem;
import me.Fupery.ArtMap.Utils.ItemUtils;

public class ArtistArtworksMenu extends ListMenu implements ChildMenu {
    private final UUID artist;
    private final String artistName;
    private ArtistMenu parent;
    private boolean adminViewing;

    public ArtistArtworksMenu(ArtistMenu parent, UUID artist, String artistName, boolean adminViewing, int page) {
        super(processTitle(artistName), page);
        this.parent = parent;
        this.adminViewing = adminViewing;
        this.artist = artist;
        this.artistName = artistName;
    }

    private static String processTitle(String artistName) {
        String name = artistName;
        String title = ChatColor.DARK_BLUE + Lang.MenuTitle.MENU_ARTWORKS.get();
        String processedName = String.format(title, name);
        if (processedName.length() <= 32)
            return processedName;
        return (name.length() <= 30) ? ChatColor.DARK_BLUE + name : ChatColor.DARK_BLUE + name.substring(0, 29);
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
            MapArt[] artworks;
            try {
                artworks = ArtMap.instance().getArtDatabase().listMapArt(this.artist);
            } catch (SQLException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
                return new Button[0];
            }
            Button[] buttons;

            if (artworks != null && artworks.length > 0) {
                buttons = new Button[artworks.length];

                for (int i = 0; i < artworks.length; i++) {
                    if(artworks[i].getArtistName() == null) {
                        artworks[i] = artworks[i].setAristName(this.artistName);
                    }
                    buttons[i] = new PreviewButton(this, artworks[i], adminViewing);
                }

            } else {
                buttons = new Button[0];
            }
            return buttons;
        });
        ArtMap.instance().getScheduler().SYNC.run(task);
        return task;
    }

    private class PreviewButton extends Button {

        private final MapArt artwork;
        private final ArtistArtworksMenu artworkMenu;

        private PreviewButton(ArtistArtworksMenu menu, MapArt artwork, boolean adminButton) {
			super(Material.FILLED_MAP);
			MapMeta meta = (MapMeta) artwork.getMapItem().getItemMeta();
			meta.setMapView(ArtMap.getMap(artwork.getMapId()));
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
				ArtMap.instance().getMenuHandler().closeMenu(player, MenuCloseReason.SWITCH);
				ArtMap.instance().getMenuHandler().openMenu(player, new ArtPieceMenu(this.artworkMenu, this.artwork, player));
            } else if (clickType == ClickType.RIGHT) {
                if (player.hasPermission("artmap.admin")) {
                    SoundCompat.BLOCK_CLOTH_FALL.play(player);
                    ArtMap.instance().getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, artwork.getMapItem()));
                } else if (adminViewing) {
                    Lang.NO_PERM.send(player);
                }
            }
        }
    }
}
