package me.Fupery.ArtMap.Compatibility.impl;

import com.wasteofplastic.askyblock.ASkyBlockAPI;
import com.wasteofplastic.askyblock.Island;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class ASkyBlockCompat implements RegionHandler {
    private ASkyBlockAPI api = null;
    private final boolean loaded;

    public ASkyBlockCompat() {
        api = ASkyBlockAPI.getInstance();
        loaded = Bukkit.getPluginManager().isPluginEnabled("ASkyBlock");
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Island island = api.getIslandAt(location);
        return island == null
                || (island.getOwner() == player.getUniqueId()
                || island.getMembers().contains(player.getUniqueId())
						|| island.getIgsFlag(Island.SettingsFlag.PLACE_BLOCKS));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        Island island = api.getIslandAt(entity.getLocation());
        return island == null
                || (island.getOwner() == player.getUniqueId()
                || island.getMembers().contains(player.getUniqueId())
						|| island.getIgsFlag(Island.SettingsFlag.ARMOR_STAND));
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
