package me.Fupery.ArtMap;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.map.MapView;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import me.Fupery.ArtMap.Command.CommandHandler;
import me.Fupery.ArtMap.Compatibility.CompatibilityManager;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Heads.HeadsCache;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.IO.Database.Database;
import me.Fupery.ArtMap.IO.Legacy.DatabaseConverter;
import me.Fupery.ArtMap.IO.Legacy.FlatDatabaseConverter;
import me.Fupery.ArtMap.IO.Legacy.V2DatabaseConverter;
import me.Fupery.ArtMap.IO.Protocol.ProtocolHandler;
import me.Fupery.ArtMap.IO.Protocol.Channel.ChannelCacheManager;
import me.Fupery.ArtMap.Listeners.EventManager;
import me.Fupery.ArtMap.Menu.Handler.MenuHandler;
import me.Fupery.ArtMap.Painting.ArtistHandler;
import me.Fupery.ArtMap.Preview.PreviewManager;
import me.Fupery.ArtMap.Recipe.RecipeLoader;
import me.Fupery.ArtMap.Utils.Reflection;
import me.Fupery.ArtMap.Utils.Scheduler;
import me.Fupery.ArtMap.api.IArtMap;
import me.Fupery.ArtMap.api.Colour.Palette;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.api.Utils.VersionHandler;

public class ArtMap extends JavaPlugin implements IArtMap {

	private static ArtMap pluginInstance = null;
	private MenuHandler menuHandler;
	private ArtistHandler artistHandler;
	private VersionHandler bukkitVersion;
	private Scheduler scheduler;
	private Database database;
	private ChannelCacheManager cacheManager;
	private RecipeLoader recipeLoader;
	private CompatibilityManager compatManager;
	private ProtocolHandler protocolHandler;
	private PixelTableManager pixelTable;
	private Configuration config;
	private EventManager eventManager;
	private PreviewManager previewManager;
	private Reflection reflection;
	private HeadsCache headsCache;
	private final ConcurrentHashMap<Location, Easel> easels;
	private Palette dyePalette;
	private boolean recipesLoaded = false;
	private boolean dbUpgradeNeeded;

	public static ArtMap instance() {
		return pluginInstance;
	}

	/**
	 * Debug method - Used for junit mocking!
	 * @param artmap The mock instance.
	 */
	public static void setInstance(ArtMap artmap) {
		pluginInstance = artmap;
	}

	public Database getArtDatabase() {
		return this.database;
	}

	public Scheduler getScheduler() {
		return this.scheduler;
	}

	public ArtistHandler getArtistHandler() {
		return this.artistHandler;
	}

	public VersionHandler getBukkitVersion() {
		return this.bukkitVersion;
	}

	public ChannelCacheManager getCacheManager() {
		return this.cacheManager;
	}

	public RecipeLoader getRecipeLoader() {
		return this.recipeLoader;
	}

	public CompatibilityManager getCompatManager() {
		return this.compatManager;
	}

	public MenuHandler getMenuHandler() {
		return this.menuHandler;
	}

	public Configuration getConfiguration() {
		return this.config;
	}

	public ProtocolHandler getProtocolManager() {
		return this.protocolHandler;
	}

	public Palette getDyePalette() {
		return this.dyePalette;
	}

	public PreviewManager getPreviewManager() {
		return this.previewManager;
	}

	public Reflection getReflection() {
		return this.reflection;
	}

	public ConcurrentHashMap<Location,Easel> getEasels() {
		return this.easels;
	}

	public PixelTableManager getPixelTable() {
		return this.pixelTable;
	}

	public HeadsCache getHeadsCache() {
		return this.headsCache;
	}

	public boolean isDBUpgradeNeeded() {
		return this.dbUpgradeNeeded;
	}

	public void setColourPalette(Palette palette) {
		this.dyePalette = palette;
	}

	public ArtMap() {
		super();
		easels = new ConcurrentHashMap<>();
	}

	//Testing constructor
	public ArtMap(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader,description,dataFolder,file);
		easels = new ConcurrentHashMap<>();
	}

	@Override
	public void onEnable() {
		try {
			pluginInstance = this;
			saveDefaultConfig();
			config = new Configuration(this);
			Lang.load(this, config);
			reflection = new Reflection();
			scheduler = new Scheduler(this);
			bukkitVersion = new VersionHandler(this);
			compatManager = new CompatibilityManager(this);
			protocolHandler = new ProtocolHandler();
			artistHandler = new ArtistHandler();
			cacheManager = new ChannelCacheManager();
			dyePalette = compatManager.getPalette();
			database = new Database(this);
			dbUpgradeNeeded = this.checkIfDatabaseUpgradeNeeded();
			this.getLogger().info(" MC version: " + bukkitVersion.toString() ) ;
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
			eventManager = new EventManager(this, bukkitVersion);
			previewManager = new PreviewManager();
			menuHandler = new MenuHandler(this);
			PluginCommand artCommand = getCommand("art");
			if(artCommand!=null) {
				artCommand.setExecutor(new CommandHandler());
			} else {
				getLogger().severe("Failed to bind /art or /artmap! Disabling...");
				getPluginLoader().disablePlugin(this);
				return;
			}
			// load the artist button cache
			headsCache = new HeadsCache(this);
		} catch( Exception e ) {
			getLogger().log(Level.SEVERE,"Failure",e);
			getPluginLoader().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		previewManager.endAllPreviews();
		artistHandler.stop();
		menuHandler.closeAll();
		eventManager.unregisterAll();
		database.close();
//        recipeLoader.unloadRecipes();
		reloadConfig();
		pluginInstance = null;
	}

	private boolean checkIfDatabaseUpgradeNeeded() {
		DatabaseConverter flatDatabaseConverter = new FlatDatabaseConverter(instance());
		DatabaseConverter v2DatabaseConverter = new V2DatabaseConverter(instance());
		if(flatDatabaseConverter.isNeeded()) {
			instance().getLogger().log(Level.WARNING,"Flat Database Coversion needed! Pleae run '/artmap convert'");
			return true;
		}
		if(v2DatabaseConverter.isNeeded()) {
			instance().getLogger().log(Level.WARNING,"V2 Database Coversion needed! Please run '/art convert'");
			return true;
		}
		return false;
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
				getLogger().log(Level.SEVERE,writeError,e);
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
	public Gson getGson(boolean pretty) {
		GsonBuilder builder = new GsonBuilder();
		if (pretty) {
			builder.setPrettyPrinting();
		}
		return builder.create();
	}

		/**
	 * Wrap retrieving map by id to keep depreciated method call in one place.
	 * 
	 * @param id The id of the map to retrieve.
	 * @return The requested MapView or null if it cannot be loaded or does not exist.
	 */
	@SuppressWarnings( "deprecation" )
	public static MapView getMap(int id) {
		return Bukkit.getMap(id);
	}

}