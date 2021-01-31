package me.Fupery.ArtMap.api.Compatability;

import org.bukkit.event.Listener;

public interface EventListener extends CompatibilityHandler, Listener {

    public void unregister();
    
}