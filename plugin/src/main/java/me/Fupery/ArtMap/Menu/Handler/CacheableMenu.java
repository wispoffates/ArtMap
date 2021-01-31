package me.Fupery.ArtMap.Menu.Handler;

import java.util.concurrent.ExecutionException;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.MenuTemplate;
import me.Fupery.ArtMap.Menu.API.MenuType;
import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.api.Config.Lang;

public abstract class CacheableMenu implements MenuTemplate {

    protected String heading;
    protected MenuType type;
    private Button[] buttons;
    private boolean open = false;

    protected CacheableMenu(String heading, InventoryType type) {
        this.heading = Lang.MenuTitle.MENU_HEADER.get() + heading;
        this.heading = (this.heading.length() > 32) ? this.heading.substring(0, 32) : this.heading;
        this.type = new MenuType(type);
    }

    protected CacheableMenu(String heading, MenuType type) {
        this.heading = Lang.MenuTitle.MENU_HEADER.get() + heading;
        this.heading = (this.heading.length() > 32) ? this.heading.substring(0, 32) : this.heading;
        this.type = type;
    }

    private void loadButtons(Inventory inventory) {
        try {
            buttons = getButtons().get();
        } catch (InterruptedException | ExecutionException e) {
            ArtMap.instance().getLogger().warning("Failure loading menu butttons.  Please submit a gitlab issue!");
        }
        for (int slot = 0; slot < buttons.length && slot < inventory.getSize(); slot++) {
            if (buttons[slot] != null) inventory.setItem(slot, buttons[slot]);
            else inventory.setItem(slot, new ItemStack(Material.AIR));
        }
    }

    Inventory open(Player player) {
        ArtMap.instance();
        Inventory inventory = this.type.createInventory(player, heading);
		ArtMap.instance().getScheduler().ASYNC.run(() -> {
			loadButtons(inventory);
			ArtMap.instance().getScheduler().SYNC.run(() -> {
				player.openInventory(inventory);
				onMenuOpenEvent(player);
				this.open = true;
			});
        });
        return inventory;
    }

    protected void refresh(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        ArtMap.instance().getScheduler().ASYNC.run(() -> {
			loadButtons(inventory);
			ArtMap.instance().getScheduler().SYNC.run(() -> {
                player.updateInventory();
                onMenuRefreshEvent(player);
            });
        });
    }

    void click(Player player, int slot, ClickType clickType) {
        if (slot >= 0 && slot < buttons.length && buttons[slot] != null)
            buttons[slot].onClick(player, clickType);
        onMenuClickEvent(player, slot, clickType);
    }

    void close(Player player, MenuCloseReason reason) {
        if (reason.shouldCloseInventory()) player.closeInventory();
        onMenuCloseEvent(player, reason);
        this.open = false;
    }

    boolean isOpen() {
        return open;
    }
}
