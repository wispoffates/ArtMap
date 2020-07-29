package me.Fupery.ArtMap.Compatibility.impl;

import javax.validation.constraints.NotNull;

import com.plotsquared.core.configuration.Captions;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.GlobalFlagContainer;
import com.plotsquared.core.plot.flag.types.BooleanFlag;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;

public class PlotSquared5Compat implements RegionHandler {

    private boolean loaded = false;
    private final ArtMapPlaceFlag place = new ArtMapPlaceFlag(false);
    private final ArtMapUseFlag use = new ArtMapUseFlag(false);

    public PlotSquared5Compat() {
        //PlotAPI api = new PlotAPI();
        GlobalFlagContainer.getInstance().addFlag(place);
        GlobalFlagContainer.getInstance().addFlag(use);
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Plot plot = Plot.getPlot(this.locationWrapper(location));

        return plot == null || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(place).booleanValue());
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        Plot plot = Plot.getPlot(this.locationWrapper(entity.getLocation()));
        return plot == null || plot.isAdded(player.getUniqueId())
                || (!plot.isDenied(player.getUniqueId()) && plot.getFlag(use).booleanValue());
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    private com.plotsquared.core.location.Location locationWrapper(Location loc) {
        return new com.plotsquared.core.location.Location(loc.getWorld().getName(),
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static class ArtMapPlaceFlag extends BooleanFlag<ArtMapPlaceFlag> {

        private static final ArtMapPlaceFlag ARTMAP_PLACE_TRUE = new ArtMapPlaceFlag(true);
        private static final ArtMapPlaceFlag ARTMAP_PLACE_FALSE = new ArtMapPlaceFlag(false);

        public ArtMapPlaceFlag(boolean value) {
            super(value,Captions.FLAG_DESCRIPTION_PLACE);
        }

        @Override
        protected ArtMapPlaceFlag flagOf(@NotNull Boolean value) {
            return value ? ARTMAP_PLACE_TRUE : ARTMAP_PLACE_FALSE;
        }
        
    }

    public static class ArtMapUseFlag extends BooleanFlag<ArtMapUseFlag> {

        private static final ArtMapUseFlag ARTMAP_USE_TRUE = new ArtMapUseFlag(true);
        private static final ArtMapUseFlag ARTMAP_USE_FALSE = new ArtMapUseFlag(false);

        public ArtMapUseFlag(boolean value) {
            super(value,Captions.FLAG_DESCRIPTION_PLACE);
        }

        @Override
        protected ArtMapUseFlag flagOf(@NotNull Boolean value) {
            return value ? ARTMAP_USE_TRUE : ARTMAP_USE_FALSE;
        }
        
    }

    
}
