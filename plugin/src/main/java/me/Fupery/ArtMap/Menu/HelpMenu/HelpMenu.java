package me.Fupery.ArtMap.Menu.HelpMenu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Menu.API.BasicMenu;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Button.StaticButton;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;

public class HelpMenu extends BasicMenu {

    public static final String CLICK = ChatColor.GREEN + Lang.BUTTON_CLICK.get();
	public static final String DELETE_NAME = ChatColor.RED + Lang.BUTTON_DELETE_NAME.get();
	public static final String DELETE_TEXT = ChatColor.RED + Lang.BUTTON_DELETE_TEXT.get();
	public static final String RENAME_NAME = ChatColor.GREEN + Lang.BUTTON_RENAME_NAME.get();
	public static final String RENAME_TEXT = ChatColor.GREEN + Lang.BUTTON_RENAME_TEXT.get();
	public static final String ACCEPT_NAME = ChatColor.GREEN + Lang.BUTTON_ACCEPT_NAME.get();
	public static final String ACCEPT_TEXT = ChatColor.GREEN + Lang.BUTTON_ACCEPT_TEXT.get();

    public HelpMenu() {
        super(ChatColor.DARK_BLUE + Lang.MenuTitle.MENU_HELP.get(), InventoryType.HOPPER);
    }

    @Override
    public Future<Button[]> getButtons() {
        MenuHandler.MenuList list = ArtMap.instance().getMenuHandler().MENU;
        return CompletableFuture.completedFuture(new Button[] {
		        new StaticButton(ArtMap.instance().getBukkitVersion().getVersion().getSign(), Lang.Array.HELP_GETTING_STARTED.get()),
		        new LinkedButton(list.RECIPE, Material.CRAFTING_TABLE, Lang.Array.HELP_RECIPES.get()),
		        new LinkedButton(list.DYES, Material.CYAN_DYE, Lang.Array.HELP_DYES.get()),
		        new LinkedButton(list.TOOLS, Material.WRITABLE_BOOK, Lang.Array.HELP_TOOLS.get()),
		        new LinkedButton(list.ARTIST, Material.PAINTING, Lang.Array.HELP_LIST.get())
        });
    }
}
