package me.Fupery.ArtMap.Compatibility.impl;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import us.talabrek.ultimateskyblock.api.IslandInfo;
import us.talabrek.ultimateskyblock.api.uSkyBlockAPI;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class USkyBlockCompat implements RegionHandler {

    private boolean loaded = false;
    private uSkyBlockAPI api = null;

    public USkyBlockCompat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("uSkyBlock");
        if (plugin instanceof uSkyBlockAPI && plugin.isEnabled()) {
            api = (uSkyBlockAPI) plugin;
            this.loaded = true;
        }
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        IslandInfo info = api.getIslandInfo(location);
        if(info != null) {
            if(!info.isLeader(player) && !info.getOnlineMembers().contains(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        IslandInfo info = api.getIslandInfo(entity.getLocation());
        if(info != null) {
            if(!info.isLeader(player) && !info.getOnlineMembers().contains(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
