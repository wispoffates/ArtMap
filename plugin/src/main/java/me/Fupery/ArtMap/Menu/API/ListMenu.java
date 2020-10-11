package me.Fupery.ArtMap.Menu.API;

import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Button.CloseButton;
import me.Fupery.ArtMap.Menu.Button.LinkedButton;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Event.MenuFactory;
import me.Fupery.ArtMap.Menu.Handler.CacheableMenu;

public abstract class ListMenu extends CacheableMenu {

    private static final char LEFT_ARROW = '\u2B05', RIGHT_ARROW = '\u27A1';

    private final String heading;
    protected int page;
	protected Optional<MenuFactory>	parent	= Optional.empty();

    public ListMenu(String heading, int page) {
        super(heading, InventoryType.CHEST);
        this.heading = heading;
        this.page = page;
    }

	public ListMenu(String heading, MenuFactory parent, int page) {
		super(heading, InventoryType.CHEST);
		this.heading = heading;
		this.page = page;
		this.parent = Optional.of(parent);
    }
    
    public String getHeading() {
        return this.heading;
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
    }

    @Override
    public void onMenuRefreshEvent(Player viewer) {
    }

    @Override
    public void onMenuClickEvent(Player viewer, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
    }

    @Override
    public Button[] getButtons() {
        Button[] listItems = getListItems();
        int maxButtons = 25;
        Button[] buttons = new Button[maxButtons + 2];

        if (page < 1) {
			if (this.parent.isPresent()) {
				String[] back = { ChatColor.RED.toString() + ChatColor.BOLD + LEFT_ARROW };
				buttons[0] = new LinkedButton(this.parent.get(), Material.MAGENTA_GLAZED_TERRACOTTA, back);
			} else {
				buttons[0] = new CloseButton();
			}
        } else {
            buttons[0] = new PageButton(false);

            if (page > 0) {
                buttons[0].setAmount(page);
            }
        }
        if (listItems == null || listItems.length < 1) return buttons;

        int start = page * maxButtons;
        int pageLength = listItems.length - start;

        if (pageLength > 0) {
            int end = (pageLength >= maxButtons) ? maxButtons : pageLength;

            System.arraycopy(listItems, start, buttons, 1, end);

            if (listItems.length > (maxButtons + start)) {
                buttons[maxButtons + 1] = new PageButton(true);

                if (page < 64) {
                    buttons[maxButtons + 1].setAmount(page + 1);
                }

            } else {
                buttons[maxButtons + 1] = null;
            }
        }
        return buttons;
    }

    protected void changePage(Player player, boolean forward) {
        if (forward) page++;
        else page--;
        refresh(player);
    }

    protected abstract Button[] getListItems();

    private class PageButton extends Button {

        boolean forward;

        private PageButton(boolean forward) {
			super(forward ? Material.MAGENTA_GLAZED_TERRACOTTA : Material.BARRIER, forward ? ChatColor.GREEN.toString() + ChatColor.BOLD + RIGHT_ARROW : ChatColor.GREEN.toString() + ChatColor.BOLD + LEFT_ARROW);
            this.forward = forward;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            SoundCompat.UI_BUTTON_CLICK.play(player);
            changePage(player, forward);
        }
    }
}
