package me.Fupery.ArtMap.Recipe;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.Fupery.ArtMap.api.Config.Lang;

public enum ArtMaterial {

	EASEL, CANVAS, MAP_ART, PAINT_BRUSH, COMPASS, COAL, FEATHER, PAINTBUCKET, SPONGE;

    private CustomItem artItem;

    public static void setupRecipes() {
        EASEL.artItem = new ArtItem.CraftableItem("EASEL", Material.ARMOR_STAND, ArtItem.EASEL_KEY)
				.name(Lang.RECIPE_EASEL_NAME).tooltip(Lang.Array.RECIPE_EASEL);

        CANVAS.artItem = new ArtItem.CraftableItem("CANVAS", Material.PAPER, ArtItem.CANVAS_KEY)
				.name(Lang.RECIPE_CANVAS_NAME)
				.tooltip(Lang.Array.RECIPE_CANVAS);

        MAP_ART.artItem = new ArtItem.ArtworkItem(-1, "Artwork", null, null);

		PAINT_BRUSH.artItem = new ArtItem.CraftableItem("PAINT_BRUSH", Material.REDSTONE_TORCH, ArtItem.PAINT_BRUSH)
                .name(Lang.RECIPE_PAINT_BRUSH_NAME).tooltip(Lang.Array.RECIPE_PAINT_BRUSH);
                
        COMPASS.artItem = new ArtItem.KitItem(Material.COMPASS, "COMPASS")
                .name(Lang.ITEM_NAME_COMPASS)
                .tooltip(Lang.Array.TOOL_COMPASS);

        COAL.artItem = new ArtItem.KitItem(Material.COAL, "COAL")
                .name(Lang.ITEM_NAME_COAL)
                .tooltip(Lang.Array.TOOL_COAL);

        FEATHER.artItem = new ArtItem.KitItem(Material.FEATHER, "FEATHER")
                .name(Lang.ITEM_NAME_FEATHER)
                .tooltip(Lang.Array.TOOL_FEATHER);

        PAINTBUCKET.artItem = new ArtItem.KitItem(Material.BUCKET, "PAINTBUCKET")
                .name(Lang.ITEM_NAME_PAINTBUCKET)
                .tooltip(Lang.Array.TOOL_PAINTBUCKET);

        SPONGE.artItem = new ArtItem.KitItem(Material.SPONGE, "SPONGE")
                .name(Lang.ITEM_NAME_SPONGE)
                .tooltip(Lang.Array.TOOL_SPONGE);

        for (ArtMaterial material : values()) material.artItem.addRecipe();
    }

    public static ArtMaterial getCraftItemType(ItemStack item) {
        for (ArtMaterial material : values()) {
            if (material.artItem.checkItem(item)) return material;
        }
        return null;
    }

    public static ArtItem.ArtworkItem getMapArt(int id, String title, String playerName, String date) {
        return new ArtItem.ArtworkItem(id, title, playerName, date);
    }

    public Material getType() {
        return artItem.getMaterial();
    }

    public ItemStack getItem() {
        return artItem.toItemStack();
    }

    public boolean isValidMaterial(ItemStack item) {
        return artItem.checkItem(item);
    }

    public Recipe getRecipe() {
        return artItem.getBukkitRecipe();
    }

    public ItemStack[] getPreview() {
        return artItem.getRecipe().getPreview();
    }
}
