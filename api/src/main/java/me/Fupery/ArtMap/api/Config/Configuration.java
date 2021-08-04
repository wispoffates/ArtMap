package me.Fupery.ArtMap.api.Config;

import org.bukkit.configuration.file.FileConfiguration;

import me.Fupery.ArtMap.api.IArtMap;


public class Configuration {
    public final String  LANGUAGE;
    public final String  WORLD;
    public final int     ARTWORK_AUTO_SAVE;
    public final boolean SWEAR_FILTER;
    public final boolean DISABLE_ACTION_BAR;
    public final boolean CUSTOM_RECIPES;
    public final boolean FORCE_ART_KIT;
    public final boolean FORCE_GUI;
    public final boolean DISABLE_PAINTBRUSH;
	public final boolean HEAD_PREFETCH;
    public final long	 HEAD_PREFETCH_DELAY;
    public final long    HEAD_PREFETCH_PERIOD;
    public final boolean HEAD_FETCH_MOJANG;
    public final int     INK_USES;
    public final boolean LIMITED_INK_USES;
	public final String  BLACKLIST;

    public Configuration(IArtMap plugin) {
        FileConfiguration configuration = plugin.getConfig();
        this.LANGUAGE = configuration.getString("language");
        String world = configuration.getString("world");
        this.WORLD = world != null ? world : "world";
        int autoSave = configuration.getInt("artworkAutoSave");
        this.ARTWORK_AUTO_SAVE = autoSave >= 30 ? autoSave : 300;
        this.SWEAR_FILTER = configuration.getBoolean("swearFilter");
        this.DISABLE_ACTION_BAR = configuration.getBoolean("disableActionBar");
        this.CUSTOM_RECIPES = configuration.getBoolean("customRecipes");
        this.FORCE_ART_KIT = configuration.getBoolean("forceArtKit");
        this.DISABLE_PAINTBRUSH = configuration.getBoolean("disablePaintbrush");
		this.FORCE_GUI = configuration.getBoolean("guiOnly", false);
		this.HEAD_PREFETCH = configuration.getBoolean("headPrefetch", true);
        this.HEAD_PREFETCH_DELAY = configuration.getLong("headPrefetchDelay", 0L);
        this.HEAD_PREFETCH_PERIOD = configuration.getLong("headPrefetchPeriod", 10000L);
        this.HEAD_FETCH_MOJANG = configuration.getBoolean("headFetchMojang", true);
        this.INK_USES = configuration.getInt("inkUses");
        this.LIMITED_INK_USES = INK_USES > 0;
		this.BLACKLIST = configuration.getString("blacklist");
    }
}
