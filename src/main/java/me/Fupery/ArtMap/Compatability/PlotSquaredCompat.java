package me.Fupery.ArtMap.Compatability;

import com.github.intellectualsites.plotsquared.api.PlotAPI;
import com.github.intellectualsites.plotsquared.plot.flag.BooleanFlag;
import com.github.intellectualsites.plotsquared.plot.object.Plot;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.Easel.EaselEvent;

class PlotSquaredCompat implements RegionHandler {

    private boolean loaded = false;
    private final BooleanFlag place = new BooleanFlag("artmap-place");
    private final BooleanFlag use = new BooleanFlag("artmap-use");

    public PlotSquaredCompat() {
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
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
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
