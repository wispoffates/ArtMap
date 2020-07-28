package me.Fupery.ArtMap.Compatibility.impl;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.containers.Flags;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.FlagPermissions;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;
import me.Fupery.ArtMap.api.Utils.Version;

public class ResidenceCompat implements RegionHandler {

    private boolean loaded = false;
    private Residence plugin;

    public ResidenceCompat() throws Exception {
        plugin = ((Residence) Bukkit.getPluginManager().getPlugin("Residence"));
        Version version = new Version(plugin);
        if (version.isLessThan(4, 8, 0, 2)) {
            loaded = false;
            plugin = null;
            throw new Exception(String.format("Invalid Residence version: " +
                    "'%s'. ArtMap requires version 4.8.0.2 or above.", version.toString()));
        }
        FlagPermissions.addFlag("artmap-place");
        FlagPermissions.addFlag("artmap-use");
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        ClaimedResidence residence = plugin.getResidenceManager().getByLoc(location);
        if (residence == null) return true;
        ResidencePermissions perms = residence.getPermissions();
        return perms.playerHas(player, "artmap-place", false);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        ClaimedResidence residence = plugin.getResidenceManager().getByLoc(entity.getLocation());
        if (residence == null) return true;
        ResidencePermissions perms = residence.getPermissions();
        return perms.playerHas(player, "artmap-use", false);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
