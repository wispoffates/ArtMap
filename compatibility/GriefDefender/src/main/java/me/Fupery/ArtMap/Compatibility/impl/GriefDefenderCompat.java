package me.Fupery.ArtMap.Compatibility.impl;

import com.griefdefender.api.Core;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class GriefDefenderCompat implements RegionHandler {
    private Core api = null;
    private boolean loaded = false;

    public GriefDefenderCompat() {
        api = GriefDefender.getCore();
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Claim claim = api.getClaimManager(player.getWorld().getUID()).getClaimAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return claim.isUserTrusted(player.getUniqueId(), TrustTypes.BUILDER);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        Location location = player.getLocation();
        Claim claim = api.getClaimManager(player.getWorld().getUID()).getClaimAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return claim.isUserTrusted(player.getUniqueId(), TrustTypes.BUILDER);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
