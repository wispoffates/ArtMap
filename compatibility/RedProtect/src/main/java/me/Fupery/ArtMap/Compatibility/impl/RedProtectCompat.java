package me.Fupery.ArtMap.Compatibility.impl;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;


import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class RedProtectCompat implements RegionHandler {

    private boolean loaded = false;
	private RedProtectAPI api;

    RedProtectCompat() {
       this.api = new RedProtectAPI();
       this.loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
		Region currentRegion = api.getRegion(location);
        return currentRegion == null || currentRegion.canBuild(player);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
		Region currentRegion = api.getRegion(entity.getLocation());
        return currentRegion == null || currentRegion.canSign(player);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
