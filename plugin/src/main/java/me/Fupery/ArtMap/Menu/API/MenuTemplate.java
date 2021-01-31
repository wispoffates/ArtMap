package me.Fupery.ArtMap.Menu.API;

import me.Fupery.ArtMap.Menu.Button.Button;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;

import java.util.concurrent.Future;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuTemplate {

    /**
     * Called after the menu has been sent to the player
     *
     * @param viewer The player viewing the menu
     */
    void onMenuOpenEvent(Player viewer);

    /**
     * Called when the menu is updated
     *
     * @param viewer The player viewing the menu
     */
    void onMenuRefreshEvent(Player viewer);

    /**
     * Called when the player clicks the menu
     *
     * @param viewer The player viewing the menu
     * @param slot The clicked slot
     * @param click The type of click performed
     */
    void onMenuClickEvent(Player viewer, int slot, ClickType click);

    /**
     * Called after the player closes the menu
     *
     * @param viewer The player viewing the menu
     * @param reason The reason the menu was closed
     */
    void onMenuCloseEvent(Player viewer, MenuCloseReason reason);

    /**
     * @return A list of itemstack buttons that fill the menu
     */
    Future<Button[]> getButtons();
}
