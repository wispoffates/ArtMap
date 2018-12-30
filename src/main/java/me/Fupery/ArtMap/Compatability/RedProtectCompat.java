package me.Fupery.ArtMap.Compatability;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import br.net.fabiozumbi12.RedProtect.Bukkit.API.RedProtectAPI;
import me.Fupery.ArtMap.Easel.EaselEvent;

class RedProtectCompat implements RegionHandler {

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
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
		Region currentRegion = api.getRegion(entity.getLocation());
        return currentRegion == null || currentRegion.canSign(player);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
