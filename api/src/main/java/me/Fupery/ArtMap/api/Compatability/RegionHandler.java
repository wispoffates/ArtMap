package me.Fupery.ArtMap.api.Compatability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Easel.ClickType;

public interface RegionHandler extends CompatibilityHandler {
    boolean checkBuildAllowed(Player player, Location location);

    boolean checkInteractAllowed(Player player, Entity entity, ClickType click);
}
