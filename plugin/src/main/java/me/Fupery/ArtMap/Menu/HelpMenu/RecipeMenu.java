package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.API.MenuType;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;
import me.Fupery.ArtMap.Recipe.ArtMaterial;
import me.Fupery.ArtMap.Utils.ItemUtils;

public class RecipeMenu extends BasicMenu implements ChildMenu {

    private static final char LEFT_ARROW = '\u2B05';

    private boolean adminMenu;

    public RecipeMenu(boolean adminMenu) {
		super(ChatColor.DARK_BLUE + Lang.MenuTitle.MENU_RECIPE.get(), new MenuType(9));
        this.adminMenu = adminMenu;
    }

    @Override
    public Future<Button[]> getButtons() {
        String[] back = { ChatColor.RED.toString() + ChatColor.BOLD + LEFT_ARROW };
        if(ArtMap.instance().getConfiguration().DISABLE_PAINTBRUSH) {
            return CompletableFuture.completedFuture(new Button[] {
                new LinkedButton(ArtMap.instance().getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back), 
                new StaticButton(Material.AIR),
                new StaticButton(ArtMap.instance().getBukkitVersion().getVersion().getSign(), Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
            });
        } else {
            return CompletableFuture.completedFuture(new Button[] {
                new LinkedButton(ArtMap.instance().getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back), 
                new StaticButton(Material.AIR),
                new StaticButton(ArtMap.instance().getBukkitVersion().getVersion().getSign(), Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
                new RecipeButton(ArtMaterial.PAINT_BRUSH),
            });
        }
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.instance().getMenuHandler().MENU.HELP.get(viewer);
    }


    private class RecipeButton extends Button {

        final ArtMaterial recipe;

        public RecipeButton(ArtMaterial material) {
            super(material.getType());
            this.recipe = material;
            ItemMeta meta = material.getItem().getItemMeta();
            List<String> lore = meta.getLore();
			lore.add("");
			lore.add(ChatColor.GREEN + Lang.RECIPE_BUTTON.get());
            if (adminMenu) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            if (adminMenu) {
                if (clickType == ClickType.LEFT) {
                    openRecipePreview(player);
                } else if (clickType == ClickType.RIGHT) {
                    ArtMap.instance().getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, recipe.getItem()));
                }
            } else {
                openRecipePreview(player);
            }
        }

        private void openRecipePreview(Player player) {
            ArtMap.instance().getMenuHandler().openMenu(player, new RecipePreview(recipe));
        }
    }
}
