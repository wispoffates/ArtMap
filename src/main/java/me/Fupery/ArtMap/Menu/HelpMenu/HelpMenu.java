package me.Fupery.ArtMap.Menu.HelpMenu;

import static org.bukkit.Material.BOOK_AND_QUILL;
import static org.bukkit.Material.INK_SACK;
import static org.bukkit.Material.PAINTING;
import static org.bukkit.Material.SIGN;
import static org.bukkit.Material.WORKBENCH;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
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
        super(ChatColor.DARK_BLUE + Lang.MENU_HELP.get(), InventoryType.HOPPER);
    }

    @Override
    public Button[] getButtons() {
        MenuHandler.MenuList list = ArtMap.getMenuHandler().MENU;
        return new Button[]{
                new StaticButton(SIGN, Lang.Array.HELP_GETTING_STARTED.get()),
                new LinkedButton(list.RECIPE, WORKBENCH, Lang.Array.HELP_RECIPES.get()),
                new LinkedButton(list.DYES, INK_SACK, 1, Lang.Array.HELP_DYES.get()),
                new LinkedButton(list.TOOLS, BOOK_AND_QUILL, Lang.Array.HELP_TOOLS.get()),
                new LinkedButton(list.ARTIST, PAINTING, Lang.Array.HELP_LIST.get())
        };
    }
}
