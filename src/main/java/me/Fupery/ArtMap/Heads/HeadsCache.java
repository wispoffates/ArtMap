package me.Fupery.ArtMap.Heads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import javax.annotation.Nullable;
import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Exception.HeadFetchException;

/**
 * Heads handler to be used with caching head textures.
 * 
 * @author wispoffates
 */
public class HeadsCache {

	static private JsonParser				parser				= new JsonParser();
	static private String					API_PROFILE_LINK	= "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final Map<UUID, TextureData>	textureCache	= Collections.synchronizedMap( new HashMap<>());
	private static File						cacheFile;
	private ArtMap plugin;

	/** Loads the cache from disk */
	public HeadsCache(ArtMap plugin) {
		this(plugin,plugin.getConfiguration().HEAD_PREFETCH);
	}

	public HeadsCache(ArtMap plugin, boolean prefetch) {
		this.plugin = plugin;
		//Load the cache file
		cacheFile = new File(plugin.getDataFolder(),"heads_cache.json");
		if(cacheFile.exists()) {
			this.loadCacheFile(cacheFile);
		}

		//init the cache
		if (prefetch) {
			plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, () -> {
				this.initHeadCache();
			}, plugin.getConfiguration().HEAD_PREFETCH_DELAY);
		}
	}

	public void updateCache(UUID playerId) throws HeadFetchException {
		this.updateTexture(playerId);
	}

	private void initHeadCache() {
		int loaded = 0;
		int mojang = 0;
		int artistsCount = 0;
		try {
			UUID[] artists = plugin.getArtDatabase().listArtists(UUID.randomUUID());
			artistsCount = artists.length;
			plugin.getLogger().info(MessageFormat.format("Async load of {0} artists started. {1} retrieved from disk cache.", artists.length, textureCache.size()));
			// skip the first one since we dummied it
			for (int i = 1; i < artists.length; i++) {
				//check cache
				if(this.isHeadCached(artists[i])) {
					loaded++;
				} else {
					//hard retrieve from mojang
					SkullMeta head = this.getHeadMeta(artists[i]);
					if (head != null) {
						mojang++;
					}
					Thread.sleep(plugin.getConfiguration().HEAD_PREFETCH_PERIOD); //go real slow
				}
			}
		} catch (Exception e) {
			//fall out so we don't error too many times
		}
		if((loaded+mojang) == 0 && artistsCount>1) {
			plugin.getLogger().warning("Could not preload any player heads! Is the server in offline mode and not behind a Bungeecord?");
		} else {
			plugin.getLogger().info(MessageFormat.format("Loaded {0} from disk cache, and {1} from mojang out of {2} artists.", loaded, mojang,artistsCount - 1));
			if(loaded+mojang < artistsCount) {
				plugin.getLogger().info("Remaining artists will be loaded when needed.");
			}
		}
	}

	/**
	 * Initialize the cache from a file.
	 * @param cacheFile The file the textures are cached in.
	 */
	private void loadCacheFile(File cacheFile) {
		try( FileReader reader = new FileReader(cacheFile); ) {
            Gson gson = ArtMap.instance().getGson(true);
            Type collectionType = new TypeToken<Map<UUID,TextureData>>() {
            }.getType();
			Map<UUID,TextureData> loadedCache = gson.fromJson(reader, collectionType);
			if(loadedCache != null && !loadedCache.isEmpty()) {
				textureCache.putAll(loadedCache);
			} else {
				ArtMap.instance().getLogger().warning("HeadCache load was null? Creating new empty cache.");
			}
            reader.close();
        } catch (Exception e) {
            ArtMap.instance().getLogger().log(Level.SEVERE, "Failure parsing head cache! Will start with an empty cache.", e);
        }
	}

	/**
	 * Save the cache to a file.
	 * @param cacheFile The file the textures should be cached in.
	 */
	private synchronized void saveCacheFile(File cacheFile) {
		try( FileWriter writer = new FileWriter(cacheFile) ){
			Gson gson = ArtMap.instance().getGson(true);
			Type collectionType = new TypeToken<Map<UUID,TextureData>>() {
			}.getType();
			gson.toJson(textureCache, collectionType, writer);
			writer.close();			
		} catch (IOException e) {
			ArtMap.instance().getLogger().log(Level.SEVERE, "Failure writing head cache!", e);
		}
	}

	/**
	 * Create a head item with the provided texture.
	 * 
	 * @param playerId The ID of the player get the skull for.
	 * 
	 * @return The Skull.
	 * @throws HeadFetchException
	 */
	public ItemStack getHead(UUID playerId) throws HeadFetchException {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = getHeadMeta(playerId);
		if (meta == null) { //try loading it the normal way
			meta = (SkullMeta) head.getItemMeta();
			OfflinePlayer player = ArtMap.instance().getServer().getOfflinePlayer(playerId);
			if(player.hasPlayedBefore()) {
				meta.setOwningPlayer(player);
				meta.setDisplayName(player.getName());
				head.setItemMeta(meta);
			}
			return head; 
		}
		head.setItemMeta(meta);
		return head;
	}

	/**
	 * Check if the provided player's texture is cached.
	 * @param playerId The UUID of the player to check.
	 * @return True if the player texture is cached.
	 */
	public boolean isHeadCached(UUID playerId) {
		return textureCache.containsKey(playerId);
	}

	/**
	 * Create a player skullMeta for the provided player id.
	 * 
	 * @param playerId The ID of the player to get the skull meta for.
	 * @return The Skull meta.
	 * @throws HeadFetchException
	 */
	@Nullable
	public SkullMeta getHeadMeta(UUID playerId) throws HeadFetchException {
		// is it in the cache?
		if (!textureCache.containsKey(playerId)) {
			this.updateTexture(playerId);
		}
		TextureData data = textureCache.get(playerId);
		if (data == null) {
			//If mojang is disabled try and get it
			if(!plugin.getConfiguration().HEAD_FETCH_MOJANG) {
				ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta meta = getHeadMeta(playerId);
				meta = (SkullMeta) head.getItemMeta();
				OfflinePlayer player = ArtMap.instance().getServer().getOfflinePlayer(playerId);
				if(player.hasPlayedBefore()) {
					meta.setOwningPlayer(player);
					meta.setDisplayName(player.getName());
					head.setItemMeta(meta);
					return meta;
				}
			}
			return null;
		}
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		// handle players without skin textures
		if (!data.texture.isEmpty()) {
			propertyMap.put("textures", new Property("textures", data.texture));
		}
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
		headMeta.setDisplayName(data.name);

		return (SkullMeta) headMeta;
	}

	protected void updateTexture(UUID playerId) throws HeadFetchException {
		//check if the user has disabled fetching heads from mojang
		if(!plugin.getConfiguration().HEAD_FETCH_MOJANG) {
			return;
		}
		Optional<TextureData> data = getSkinUrl(playerId);
		if(data.isPresent()) {
			textureCache.put(playerId, data.get());
			this.saveCacheFile(cacheFile);
		}
	}

	/**
	 * Retrieve the current cache size.
	 * 
	 * @return The current cache size.
	 */
	public int getCacheSize() {
		return textureCache.size();
	}

	/*
	 * HTTP Methods
	 */
	private static String getContent(String link) throws HeadFetchException {
		BufferedReader br = null;
		try {
			URL url = new URL(link);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuffer sb = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				sb.append(inputLine);
			}
			br.close();
			return sb.toString();
		} catch (MalformedURLException e) {
			ArtMap.instance().getLogger().log(Level.SEVERE, "Failure getting head!", e);
			throw new HeadFetchException("Failure getting head!",e);
		} catch (IOException e) {
			ArtMap.instance().getLogger().info("Error retrieving head texture.  Server is likely over API limit temporarily.  The head will be fetched on use later.");
			throw new HeadFetchException("Error retrieving head texture.  Server is likely over API limit temporarily.  The head will be fetched on use later.",e);
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				//don't care on close.
			}
		}
	}

	private static Optional<TextureData> getSkinUrl(UUID uuid) throws HeadFetchException {
		try {
			String id = uuid.toString().replace("-", "");
			String json = getContent(API_PROFILE_LINK + id);
			JsonObject o = parser.parse(json).getAsJsonObject();
			String name = o.get("name").getAsString();
			JsonArray jArray= o.get("properties").getAsJsonArray();
			String jsonBase64 = null;
			if(jArray.size() > 0) {
				jsonBase64 = jArray.get(0).getAsJsonObject().get("value").getAsString();
			} else {
				return Optional.empty();
			}
			return Optional.of(new TextureData(name, jsonBase64));
		} catch ( Exception e ) {
			ArtMap.instance().getLogger().log(Level.SEVERE, "Failure parsing skin texture json.", e);
			return Optional.empty();
		}
	}

	private static class TextureData {
		public String	name;
		public String	texture;

		public TextureData(String name, String texture) {
			this.name = name;
			this.texture = texture;
		}
	}

}
