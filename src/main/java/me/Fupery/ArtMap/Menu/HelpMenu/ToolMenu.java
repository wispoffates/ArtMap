package me.Fupery.ArtMap.Menu.HelpMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.Fupery.InvMenu.Utils.MenuType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;

public class ToolMenu extends BasicMenu implements ChildMenu {

    private static final char LEFT_ARROW = '\u2B05';

    public ToolMenu() {
		super(ChatColor.DARK_BLUE + Lang.MENU_TOOLS.get(), new MenuType(9));
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtMap.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    public Button[] getButtons() {
    	String[] back = { ChatColor.RED.toString() + ChatColor.BOLD + LEFT_ARROW };
        return new Button[]{
		        new LinkedButton(ArtMap.getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back),
		        new StaticButton(Material.AIR),
                new StaticButton(Material.OAK_SIGN, Lang.Array.INFO_TOOLS.get()),
		        new LinkedButton(ArtMap.getMenuHandler().MENU.DYES, Material.RED_DYE, Lang.Array.TOOL_DYE.get()),
                new StaticButton(Material.BUCKET, Lang.Array.TOOL_PAINTBUCKET.get()),
                new StaticButton(Material.COAL, Lang.Array.TOOL_COAL.get()),
                new StaticButton(Material.FEATHER, Lang.Array.TOOL_FEATHER.get()),
                new StaticButton(Material.COMPASS, Lang.Array.TOOL_COMPASS.get())
        };
    }
}
