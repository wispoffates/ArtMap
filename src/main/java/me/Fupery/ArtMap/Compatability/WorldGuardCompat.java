package me.Fupery.ArtMap.Compatability;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.EaselEvent;
import me.Fupery.ArtMap.Utils.Version;

public class WorldGuardCompat implements RegionHandler {
    private WorldGuardPlugin worldGuardPlugin;
    boolean loaded = false;

    WorldGuardCompat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        Version version = new Version(plugin);
        if (version.isLessThan(7,0,0)) {
            ArtMap.instance().getLogger().warning(String.format("Invalid WorldGuard version: " +
                    "'%s'. ArtMap requires version 7.0.0 or above.", version.toString()));
            loaded = false;
            return;
        }
        this.worldGuardPlugin = WorldGuardPlugin.inst();
        this.loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        WorldGuardPlugin wg = worldGuardPlugin;
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));
        return query.testState(BukkitAdapter.adapt(location), WorldGuardPlugin.inst().wrapPlayer(player), Flags.BUILD) && (player.hasPermission("artmap.region.member") ||
                ((set.size() > 0 && set.isOwnerOfAll(wg.wrapPlayer(player)))
                        || set.testState(wg.wrapPlayer(player), Flags.BUILD)));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        WorldGuardPlugin wg = worldGuardPlugin;
        RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(entity.getLocation()));
        return (set.size() > 0 && set.isMemberOfAll(wg.wrapPlayer(player)))
                || set.testState(wg.wrapPlayer(player), Flags.INTERACT);
    }

    @Override
    public boolean isLoaded() {
        return this.loaded;
    }
}

