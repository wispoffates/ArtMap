package me.Fupery.ArtMap;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.Fupery.ArtMap.Colour.BasicPalette;
import me.Fupery.ArtMap.Colour.Palette;
import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.Compatability.CompatibilityManager;
import me.Fupery.ArtMap.Config.Configuration;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.EaselMap;
import me.Fupery.ArtMap.Heads.Heads;
import me.Fupery.ArtMap.IO.ErrorLogger;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.IO.Database.Database;
import me.Fupery.ArtMap.IO.Legacy.OldDatabaseConverter;
import me.Fupery.ArtMap.IO.Protocol.ProtocolHandler;
import me.Fupery.ArtMap.IO.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.Listeners.EventManager;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Painting.ArtistHandler;
import me.Fupery.ArtMap.Preview.PreviewManager;
import me.Fupery.ArtMap.Recipe.RecipeLoader;
import me.Fupery.ArtMap.Utils.Scheduler;
import me.Fupery.ArtMap.Utils.VersionHandler;

public class ArtMap extends JavaPlugin {

	private static SoftReference<ArtMap>	pluginInstance	= null;
	private MenuHandler						menuHandler;
	private ArtistHandler					artistHandler;
	private VersionHandler					bukkitVersion;
	private Scheduler						scheduler;
	private Database						database;
	private ChannelCacheManager				cacheManager;
	private RecipeLoader					recipeLoader;
	private CompatibilityManager			compatManager;
	private ProtocolHandler					protocolHandler;
	private PixelTableManager				pixelTable;
	private Configuration					config;
	private EventManager					eventManager;
	private PreviewManager					previewManager;
	private EaselMap						easels;
	private Palette							dyePalette;
	private boolean							recipesLoaded	= false;
	private boolean							disabled;

	public static Database getArtDatabase() {
		return instance().database;
	}

	public static ArtMap instance() {
		if (pluginInstance == null || pluginInstance.get() == null) {
			pluginInstance = new SoftReference<>((ArtMap) Bukkit.getPluginManager().getPlugin("ArtMap"));
		}
		return pluginInstance.get();
	}

	public static Scheduler getScheduler() {
		return instance().scheduler;
	}

	public static ArtistHandler getArtistHandler() {
		return instance().artistHandler;
	}

	public static VersionHandler getBukkitVersion() {
		return instance().bukkitVersion;
	}

	public static ChannelCacheManager getCacheManager() {
		return instance().cacheManager;
	}

	public static RecipeLoader getRecipeLoader() {
		return instance().recipeLoader;
	}

	public static CompatibilityManager getCompatManager() {
		return instance().compatManager;
	}

	public static MenuHandler getMenuHandler() {
		return instance().menuHandler;
	}

	public static Configuration getConfiguration() {
		return instance().config;
	}

	public static ProtocolHandler getProtocolManager() {
		return instance().protocolHandler;
	}

	public static Palette getDyePalette() {
		return instance().dyePalette;
	}

	public static PreviewManager getPreviewManager() {
		return instance().previewManager;
	}

	public static EaselMap getEasels() {
		return instance().easels;
	}

	public static PixelTableManager getPixelTable() {
		return instance().pixelTable;
	}

	public static boolean isDisabled() {
		return instance().disabled;
	}

	public void setColourPalette(Palette palette) {
		this.dyePalette = palette;
	}

	@Override
	public void onEnable() {
		pluginInstance = new SoftReference<>(this);
		saveDefaultConfig();
		compatManager = new CompatibilityManager(this);
		config = new Configuration(this, compatManager);
		scheduler = new Scheduler(this);
		bukkitVersion = new VersionHandler();
		protocolHandler = new ProtocolHandler();
		artistHandler = new ArtistHandler();
		cacheManager = new ChannelCacheManager();
		Lang.load(this, config);
		dyePalette = new BasicPalette();
		if ((database = Database.build(this)) == null) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		new OldDatabaseConverter(this).convertDatabase();
		if ((pixelTable = PixelTableManager.buildTables(this)) == null) {
			getLogger().warning(Lang.INVALID_DATA_TABLES.get());
			getPluginLoader().disablePlugin(this);
			return;
		}
		if (!recipesLoaded) {
			recipeLoader = new RecipeLoader(this, config);
			recipeLoader.loadRecipes();
			recipesLoaded = true;
		}
		easels = new EaselMap();
		eventManager = new EventManager(this, bukkitVersion);
		previewManager = new PreviewManager();
		menuHandler = new MenuHandler(this);
		getCommand("art").setExecutor(new CommandHandler());
		ArtMap.instance();
		// load the artist button cache
		if (ArtMap.getConfiguration().HEAD_PREFETCH) {
			this.getServer().getScheduler().runTaskLaterAsynchronously(this, () -> {
				this.initHeadCache();
			}, ArtMap.getConfiguration().HEAD_PREFETCH_DELAY);
		}

		disabled = false;
	}

	@Override
	public void onDisable() {
		disabled = true;
		previewManager.endAllPreviews();
		artistHandler.stop();
		menuHandler.closeAll();
		eventManager.unregisterAll();
		database.close();
//        recipeLoader.unloadRecipes();
		reloadConfig();
		pluginInstance = null;
	}

	private void initHeadCache() {
		UUID[] artists = ArtMap.getArtDatabase().listArtists(UUID.randomUUID());
		this.getLogger().info(MessageFormat.format("Async load of {0} artists started.", artists.length - 1));
		int loaded = 0;
		// skip the first one since we dummied it
		for (int i = 1; i < artists.length; i++) {
			ItemStack head = Heads.getHead(artists[i]);
			if (head != null) {
				loaded++;
			}
			// slow down so we avoid running into mojang api limits
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// Do not care
			}
		}
		if(loaded == 0 && artists.length>0) {
			this.getLogger().warning("Could not preload any player heads! Is the server in offline mode and not behind a Bungeecord?");
		} else {
			this.getLogger().info(MessageFormat.format("Async loaded {0} of {1} artists.", loaded, artists.length - 1));
		}
	}

	public boolean writeResource(String resourcePath, File destination) {
		String writeError = String.format("Cannot write resource '%s' to destination '%s'.", resourcePath, destination.getAbsolutePath());
		if (!destination.exists())
			try {
				if (destination.createNewFile()) {
					Files.copy(getResource(resourcePath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
				} else {
					getLogger().warning(writeError + " Error: Destination cannot be created.");
				}
			} catch (IOException e) {
				ErrorLogger.log(e, writeError);
				return false;
			}
		return true;
	}

	public Reader getTextResourceFile(String fileName) {
		return getTextResource(fileName);
	}

	/**
	 * Retrieve primed gson instance.
	 * 
	 * @param pretty Enable pretty print.
	 * @return GSON instance.
	 */
	public static Gson getGson(boolean pretty) {
		GsonBuilder builder = new GsonBuilder();
		if (pretty) {
			builder.setPrettyPrinting();
		}
		return builder.create();
	}
}