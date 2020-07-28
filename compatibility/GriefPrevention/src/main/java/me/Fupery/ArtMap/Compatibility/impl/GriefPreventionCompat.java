package me.Fupery.ArtMap.Compatibility.impl;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class GriefPreventionCompat implements RegionHandler {
    private GriefPrevention api = null;
    private boolean loaded = false;

    public GriefPreventionCompat() {
        api = GriefPrevention.instance;
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        return (api.allowBuild(player, location) == null);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        Claim claim = api.dataStore.getClaimAt(entity.getLocation(), false, null);
        if(claim == null) {
            return true;
        }
        return (claim.allowAccess(player) == null);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
