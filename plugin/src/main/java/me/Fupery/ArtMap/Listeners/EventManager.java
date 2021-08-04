package me.Fupery.ArtMap.Listeners;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.Fupery.ArtMap.api.Utils.VersionHandler;

import java.util.HashSet;
import java.util.Set;

public class EventManager {
    private final Set<RegisteredListener> listeners;

    public EventManager(JavaPlugin plugin, VersionHandler version) {
        listeners = new HashSet<>();
        listeners.add(new PlayerInteractListener());
        listeners.add(new PlayerInteractEaselListener());
        listeners.add(new PlayerQuitListener());
        listeners.add(new ChunkUnloadListener());
        listeners.add(new PlayerCraftListener());
        listeners.add(new InventoryInteractListener());
        listeners.add(new MapInitializeListener());
        listeners.add(new PlayerSwapHandListener());
        listeners.add(new PlayerDismountListener());
        listeners.add(new PlayerJoinEventListener());
		PlayerCommandPreListener compre = new PlayerCommandPreListener();
		compre.getBlacklist();
        listeners.add(compre);
        PluginManager manager = plugin.getServer().getPluginManager();
        for (RegisteredListener listener : listeners) manager.registerEvents(listener, plugin);
    }

    public void unregisterAll() {
        listeners.forEach(RegisteredListener::unregister);
    }
}
