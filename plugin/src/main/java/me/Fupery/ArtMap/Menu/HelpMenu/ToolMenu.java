package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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

public class ToolMenu extends BasicMenu implements ChildMenu {

    private static final char LEFT_ARROW = '\u2B05';

    public ToolMenu() {
		super(ChatColor.DARK_BLUE + Lang.MenuTitle.MENU_TOOLS.get(), new MenuType(9));
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.instance().getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    public Future<Button[]> getButtons() {
    	String[] back = { ChatColor.RED.toString() + ChatColor.BOLD + LEFT_ARROW };
        return CompletableFuture.completedFuture(new Button[] {
		        new LinkedButton(ArtMap.instance().getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back),
		        new StaticButton(Material.AIR),
                new StaticButton(ArtMap.instance().getBukkitVersion().getVersion().getSign(), Lang.Array.INFO_TOOLS.get()),
		        new LinkedButton(ArtMap.instance().getMenuHandler().MENU.DYES, Material.CYAN_DYE, Lang.Array.TOOL_DYE.get()),
                new StaticButton(ArtMaterial.PAINTBUCKET.getItem()),
                new StaticButton(ArtMaterial.COAL.getItem()),
                new StaticButton(ArtMaterial.FEATHER.getItem()),
                new StaticButton(ArtMaterial.COMPASS.getItem()),
                new StaticButton(ArtMaterial.SPONGE.getItem())
        });
    }
}
