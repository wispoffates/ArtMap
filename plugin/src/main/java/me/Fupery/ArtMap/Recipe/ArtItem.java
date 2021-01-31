package me.Fupery.ArtMap.Recipe;

import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.YELLOW;

import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;

public class ArtItem {

    public static final String ARTWORK_TAG = ChatColor.AQUA.toString() + ChatColor.ITALIC + "Player Artwork";
    public static final String CANVAS_KEY = ChatColor.AQUA.toString() + ChatColor.ITALIC + "ArtMap Canvas";
    public static final String EASEL_KEY = ChatColor.AQUA.toString() + ChatColor.ITALIC + "ArtMap Easel";
    public static final String KIT_KEY = ChatColor.DARK_GRAY + "[ArtKit]";
    public static final String PREVIEW_KEY = ChatColor.AQUA.toString() + ChatColor.ITALIC + "Preview Artwork";
    public static final String COPY_KEY = ChatColor.AQUA.toString() + ChatColor.ITALIC + "Artwork Copy";
    public static final String PAINT_BRUSH = ChatColor.AQUA.toString() + ChatColor.ITALIC + "Paint Brush";
    
    private ArtItem() {
        //hide consstructor
    }

	//private static final char BULLET_POINT = '\u2022';

    static class CraftableItem extends CustomItem {

        public CraftableItem(String itemName, Material material, String uniqueKey) {
            super(material, KIT_KEY, uniqueKey);
            try {
                recipe(ArtMap.instance().getRecipeLoader().getRecipe(itemName.toUpperCase()));
            } catch (RecipeLoader.InvalidRecipeException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
            }
        }
    }

    public static class ArtworkItem extends CustomItem {
        public ArtworkItem(int id, String title, String artistName, String date) {
			super(new ItemStack(Material.FILLED_MAP), ARTWORK_TAG);
			MapMeta meta = (MapMeta) this.stack.get().getItemMeta();
			meta.setMapView(ArtMap.getMap(id));
			this.stack.get().setItemMeta(meta);
            String name = artistName;
            name(title);
            String artist = GOLD + String.format(Lang.RECIPE_ARTWORK_ARTIST.get(), (YELLOW + name));
            tooltip(artist, String.valueOf(DARK_GREEN) + ITALIC + date);
        }
    }

    public static class InProgressArtworkItem extends CustomItem {
        public InProgressArtworkItem(int id) {
			super(new ItemStack(Material.FILLED_MAP), ARTWORK_TAG);
			MapMeta meta = (MapMeta) this.stack.get().getItemMeta();
			meta.setMapView(ArtMap.getMap(id));
			this.stack.get().setItemMeta(meta);
            name("In Progress");
            String artist = GOLD + String.format(Lang.RECIPE_ARTWORK_ARTIST.get(), (YELLOW + "In Progress"));
            tooltip(artist);
        }
    }

    public static class KitItem extends CustomItem {
        KitItem(Material material, String name) {
            super(material, KIT_KEY, name);
        }
    }
}
