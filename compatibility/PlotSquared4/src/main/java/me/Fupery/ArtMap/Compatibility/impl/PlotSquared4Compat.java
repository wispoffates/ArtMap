package me.Fupery.ArtMap.Compatibility.impl;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag;
import com.github.intellectualsites.plotsquared.plot.object.Plot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;
import me.Fupery.ArtMap.api.Utils.Version;

public class PlotSquared4Compat implements RegionHandler {

    private boolean loaded = false;
    private final BooleanFlag place = new BooleanFlag("artmap-place");
    private final BooleanFlag use = new BooleanFlag("artmap-use");

    public PlotSquared4Compat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlotSquared");
        Version version = new Version(plugin);
        //only loaded v5 support if the version is below 4
        if (version.isGreaterOrEqualTo(5,0,0)) {
            loaded = false;
            return;
        }
        PlotAPI api = new PlotAPI();
        api.addFlag(place);
        api.addFlag(use);
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Plot plot = Plot.getPlot(this.locationWrapper(location));

        return plot == null
                || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(place, false));
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        Plot plot = Plot.getPlot(this.locationWrapper(entity.getLocation()));
        return plot == null
                || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(use, false));
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private com.github.intellectualsites.plotsquared.plot.object.Location locationWrapper(Location loc) {
        return new com.github.intellectualsites.plotsquared.plot.object.Location(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(),loc.getBlockZ());
    }
}
