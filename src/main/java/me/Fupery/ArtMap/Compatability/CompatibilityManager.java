package me.Fupery.ArtMap.Compatability;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.EaselEvent;

public class CompatibilityManager implements RegionHandler {
    private List<RegionHandler> regionHandlers;
    private ReflectionHandler reflectionHandler;

    public CompatibilityManager(JavaPlugin plugin) {
        regionHandlers = new ArrayList<>();
        loadRegionHandler("WorldGuard",WorldGuardCompat.class);
		loadRegionHandler("Factions",FactionsCompat.class);
        loadRegionHandler("GriefPrevention",GriefPreventionCompat.class);
		loadRegionHandler("RedProtect",RedProtectCompat.class);
        loadRegionHandler("ASkyBlock",ASkyBlockCompat.class);
        loadRegionHandler("uSkyBlock",USkyBlockCompat.class);
        loadRegionHandler("BentoBox",BentoBoxCompat.class);
		loadRegionHandler("PlotSquared",PlotSquaredCompat.class);
        loadRegionHandler("Residence",ResidenceCompat.class);
        loadRegionHandler("Towny",TownyCompat.class);
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
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
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

    private void loadRegionHandler(String pluginName, Class<? extends RegionHandler> handlerClass) {
        try {
            if (ArtMap.instance().getServer().getPluginManager().isPluginEnabled(pluginName)) {
                RegionHandler handler = handlerClass.newInstance();
                if (handler.isLoaded()) {
                    regionHandlers.add(handler);
                }
            } else {
                ArtMap.instance().getLogger().info(pluginName + " not detected.  Hooks skipped.");
            }
        } catch (Throwable exception) {
            ArtMap.instance().getLogger().log(Level.SEVERE,"Exception loading region handler for " + pluginName + 
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
