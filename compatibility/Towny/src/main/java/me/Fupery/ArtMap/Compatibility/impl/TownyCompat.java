package me.Fupery.ArtMap.Compatibility.impl;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;
import me.Fupery.ArtMap.api.Utils.Version;

public class TownyCompat implements RegionHandler {
    private boolean loaded = false;

    TownyCompat() throws Exception {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Towny");
        Version version = new Version(plugin);
        if (version.isLessThan(0,92)) {
            loaded = false;
           throw new Exception(String.format("Invalid Towny version: " +
                    "'%s'. ArtMap requires version 0.92 or above.", version.toString()));
        }
        Towny.getPlugin();
        this.loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        return PlayerCacheUtil.getCachePermission(player, location, Material.ARMOR_STAND,
                TownyPermission.ActionType.BUILD);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        return PlayerCacheUtil.getCachePermission(player, entity.getLocation(), Material.ARMOR_STAND, TownyPermission.ActionType.ITEM_USE);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}

