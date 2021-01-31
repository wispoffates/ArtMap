package me.Fupery.ArtMap.Menu.Handler;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.ChildMenu;
import me.Fupery.ArtMap.Menu.Event.MenuCloseReason;
import me.Fupery.ArtMap.Menu.Event.MenuFactory;
import me.Fupery.ArtMap.Menu.Event.MenuListener;
import me.Fupery.ArtMap.Menu.HelpMenu.*;
import me.Fupery.ArtMap.api.Config.Lang;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class MenuHandler {
    public final MenuList MENU = new MenuList();
    private final ConcurrentHashMap<UUID, CacheableMenu> openMenus = new ConcurrentHashMap<>();
    
    public MenuHandler(JavaPlugin plugin) {
        new MenuListener(this, plugin);
    }

    public boolean isTrackingPlayer(Player player) {
        String invTitle = player.getOpenInventory().getTitle();
        return invTitle.startsWith(Lang.MenuTitle.MENU_HEADER.get());
    }

    public void openMenu(Player viewer, CacheableMenu menu) {
        if (isTrackingPlayer(viewer)) {
            closeMenu(viewer, MenuCloseReason.SWITCH);
        } else {
            viewer.closeInventory();//todo check if this works
        }
        openMenus.put(viewer.getUniqueId(),menu);
        menu.open(viewer);
    }

    public void fireClickEvent(Player viewer, int slot, ClickType clickType) {
        if (!isTrackingPlayer(viewer)) return;
        this.openMenus.get(viewer.getUniqueId()).click(viewer, slot, clickType);
    }

    public void refreshMenu(Player viewer) {
        if (!isTrackingPlayer(viewer)) return;
        this.openMenus.get(viewer.getUniqueId()).refresh(viewer);
    }

    public void closeMenu(Player viewer, MenuCloseReason reason) {
        if (!isTrackingPlayer(viewer)) return;
        CacheableMenu menu = openMenus.get(viewer.getUniqueId());
        menu.close(viewer, reason);
        if (menu instanceof ChildMenu && reason == MenuCloseReason.BACK) {
            openMenu(viewer, ((ChildMenu) menu).getParent(viewer));
        } else {
            openMenus.remove(viewer.getUniqueId());
        }
    }

    public void closeAll() {
        for (UUID uuid : openMenus.keySet()) closeMenu(Bukkit.getPlayer(uuid), MenuCloseReason.SYSTEM);
        openMenus.clear();
    }

    public static class MenuList {
        public MenuFactory HELP = new StaticMenuFactory(HelpMenu::new);
        public MenuFactory DYES = new StaticMenuFactory(DyeMenu::new);
        public MenuFactory TOOLS = new StaticMenuFactory(ToolMenu::new);
        public MenuFactory ARTIST = new DynamicMenuFactory(ArtistMenu::new);
        public MenuFactory RECIPE = new ConditionalMenuFactory(new ConditionalMenuFactory.ConditionalGenerator() {
            @Override
            public CacheableMenu getConditionTrue() {
                return new RecipeMenu(true);
            }

            @Override
            public CacheableMenu getConditionFalse() {
                return new RecipeMenu(false);
            }

            @Override
            public boolean evaluateCondition(Player viewer) {
                return viewer.hasPermission("artmap.admin");
            }
        });
    }
}
