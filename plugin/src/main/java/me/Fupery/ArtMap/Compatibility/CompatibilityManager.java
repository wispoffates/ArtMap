package me.Fupery.ArtMap.Compatibility;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatibility.impl.ASkyBlockCompat;
import me.Fupery.ArtMap.Compatibility.impl.BentoBoxCompat;
import me.Fupery.ArtMap.Compatibility.impl.FactionsCompat;
import me.Fupery.ArtMap.Compatibility.impl.GriefPreventionCompat;
import me.Fupery.ArtMap.Compatibility.impl.PlotSquared4Compat;
import me.Fupery.ArtMap.Compatibility.impl.PlotSquared5Compat;
import me.Fupery.ArtMap.Compatibility.impl.RedProtectCompat;
import me.Fupery.ArtMap.Compatibility.impl.ResidenceCompat;
import me.Fupery.ArtMap.Compatibility.impl.SabreFactionsCompat;
import me.Fupery.ArtMap.Compatibility.impl.TownyCompat;
import me.Fupery.ArtMap.Compatibility.impl.USkyBlockCompat;
import me.Fupery.ArtMap.Compatibility.impl.WorldGuardCompat;
import me.Fupery.ArtMap.api.Compatability.ReflectionHandler;
import me.Fupery.ArtMap.api.Compatability.RegionHandler;
import me.Fupery.ArtMap.api.Easel.ClickType;
import me.Fupery.ArtMap.api.Utils.Version;

public class CompatibilityManager implements RegionHandler {
    private List<RegionHandler> regionHandlers;
    private ReflectionHandler reflectionHandler;

    public CompatibilityManager(JavaPlugin plugin) {
        regionHandlers = new ArrayList<>();
        loadRegionHandler("WorldGuard",WorldGuardCompat.class, "WorldGuard 7");
        //Disable as it is 1.12 and lower
        //loadRegionHandler("Factions",FactionsCompat.class, "Factions");
        loadRegionHandler("Factions",SabreFactionsCompat.class, "Sabre Factions");
        loadRegionHandler("GriefPrevention",GriefPreventionCompat.class,"Grief Prevention");
        loadRegionHandler("RedProtect",RedProtectCompat.class, "Red Protect");
        //likely can be removed as 1.12 and lower
        loadRegionHandler("ASkyBlock",ASkyBlockCompat.class, "ASkyBlock");
        loadRegionHandler("uSkyBlock",USkyBlockCompat.class, "uSkyBlock");
        loadRegionHandler("BentoBox",BentoBoxCompat.class, "BentoBox/BSkyBlock");
        loadRegionHandler("PlotSquared",PlotSquared4Compat.class, "Plot Squared 4", new Version(4), new Version(5));
        loadRegionHandler("PlotSquared",PlotSquared5Compat.class, "Plot Squared 5", new Version(5), new Version(9999));
        loadRegionHandler("Residence",ResidenceCompat.class, "Residence");
        loadRegionHandler("Towny",TownyCompat.class, "Towny");
        reflectionHandler = loadReflectionHandler();
        if (!(reflectionHandler instanceof VanillaReflectionHandler))
            plugin.getLogger().info(String.format("%s reflection handler enabled.",
                    reflectionHandler.getClass().getSimpleName().replace("Compat", "")));
        for (RegionHandler regionHandler : regionHandlers) {
            plugin.getLogger().info(String.format("%s hooks enabled.",
                    regionHandler.getClass().getSimpleName().replace("Compat", "")));
        }
    }

    public boolean isPluginLoaded(String pluginName) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        if (player.hasPermission("artmap.admin")) return true; //admins can override
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkBuildAllowed(player, location)) return false;
        }
        return true;
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, ClickType click) {
        if (checkBuildAllowed(player, entity.getLocation())) return true; //builders can override
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkInteractAllowed(player, entity, click)) return false;
        }
        return true;
    }

    public ReflectionHandler getReflectionHandler() {
        return reflectionHandler;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    private ReflectionHandler loadReflectionHandler() {
        ReflectionHandler denizenHandler = new DenizenCompat();
        if (denizenHandler.isLoaded()) return denizenHandler;
        ReflectionHandler iDisguiseHandler = new iDisguiseCompat();
        if (iDisguiseHandler.isLoaded()) return iDisguiseHandler;
        return new VanillaReflectionHandler();
    }

    private void loadRegionHandler(String pluginName, Class<? extends RegionHandler> handlerClass, String description) {
        try {
            if (ArtMap.instance().getServer().getPluginManager().isPluginEnabled(pluginName)) {
                RegionHandler handler = handlerClass.newInstance();
                if (handler.isLoaded()) {
                    regionHandlers.add(handler);
                }
            } else {
                ArtMap.instance().getLogger().info(description + " not detected.  Hooks skipped.");
            }
        } catch (Throwable exception) {
            ArtMap.instance().getLogger().log(Level.SEVERE,"Exception loading region handler for " + description + 
                " please create a ticket on the Artmap gitlab page with the version of the plugin you are using!",exception);
        }
    }

    /**
     * Load the region handler if pluginName is loaded and meets ther version requirements.
     * @param pluginName The plugin to check.
     * @param handlerClass The class to load if it neets requierments.
     * @param description The description to print when loaded or fails.
     * @param lower The lowest version [inclusive] to load the handler.
     * @param upper The Upper version [exclusive] to load the handler.
     */
    private void loadRegionHandler(String pluginName, Class<? extends RegionHandler> handlerClass, String description, Version lower, Version upper) {
        try {
            if (ArtMap.instance().getServer().getPluginManager().isPluginEnabled(pluginName)) {
                Version pluginVersion = new Version(ArtMap.instance().getServer().getPluginManager().getPlugin(pluginName));
                if(lower.compareTo(pluginVersion) == 0 && upper.compareTo(pluginVersion) > 0) {
                    RegionHandler handler = handlerClass.newInstance();
                    if (handler.isLoaded()) {
                        regionHandlers.add(handler);
                    }
                }
            } else {
                ArtMap.instance().getLogger().info(pluginName + " not detected.  Hooks skipped.");
            }
        } catch (Throwable exception) {
            ArtMap.instance().getLogger().log(Level.SEVERE,"Exception loading region handler for " + description + 
                " please create a ticket on the Artmap gitlab page with the version of the plugin you are using!",exception);
        }
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Plugin compatability hooks: ");
        for (RegionHandler regionHandler : regionHandlers) {
            sb.append(regionHandler.getClass().getSimpleName() + " [LOADED:" + regionHandler.isLoaded() + "], ");
        }
        sb.append("Reflection Handler: " + reflectionHandler.getClass());
        return sb.toString();
    }
}
