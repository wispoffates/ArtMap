package me.Fupery.ArtMap.Menu.HelpMenu;

import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Recipe.ArtMaterial;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class RecipePreview extends BasicMenu {

    private final ArtMaterial recipe;

    public RecipePreview(ArtMaterial recipe) {
        super(String.format(Lang.MenuTitle.RECIPE_HEADER.get(), recipe.name().toLowerCase()),
                InventoryType.DISPENSER);
        this.recipe = recipe;
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
        viewer.updateInventory();
    }

    @Override
    public Future<Button[]> getButtons() {
        FutureTask<Button[]> task = new FutureTask<> (()->{
            ItemStack[] preview = recipe.getPreview();
            Button[] buttons = new Button[preview.length];

            for (int i = 0; i < preview.length; i++) {
                buttons[i] = preview[i] != null ? new StaticButton(preview[i]) : null;
            }
            return buttons;
        });
        ArtMap.instance().getScheduler().ASYNC.run(task);
        return task;
    }
}
