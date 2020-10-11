package me.Fupery.ArtMap.Menu.API;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

/**
 * Wrapper for menu types that supports both InventoryType and inventory size
 */
public class MenuType {

    private final InventoryType inventoryType;
    private final int size;

    public MenuType(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        this.size = (inventoryType != null) ? inventoryType.getDefaultSize() : -1;
    }

    public MenuType(int size) {
        this.size = size;
        this.inventoryType = null;
    }

    public Inventory createInventory(Player player, String heading) {
        if (inventoryType != null) return Bukkit.createInventory(player, inventoryType, heading);
        else if (size > 0) return Bukkit.createInventory(player, size, heading);
        else return null;
    }

    public int getDefaultSize() {
        return size;
    }
}
