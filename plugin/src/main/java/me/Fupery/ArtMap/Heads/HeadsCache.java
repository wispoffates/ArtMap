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

	private static JsonParser				parser				= new JsonParser();
	private static String					API_PROFILE_LINK	= "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static final Map<UUID, TextureData>	textureCache	= Collections.synchronizedMap( new HashMap<>());
	/** Map to convert names to UUIDs for players that have never logged in to the server.
	 * This is temporary till DB schema update that adds names to db.
	 */
	private static final Map<String, UUID> nameToUUID = new HashMap<>();
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
		//int the nameToUUID
		textureCache.entrySet().stream().forEach(entry -> {
			nameToUUID.put(entry.getValue().name, entry.getKey());
		});
	}

	public void updateCache(UUID playerId) throws HeadFetchException {
		this.updateTexture(playerId);
	}

	private void initHeadCache() {
		int cached = 0;
		int mojang = 0;
		int server = 0;
		int failed = 0;
		int artistsCount = 0;
		try {
			UUID[] artists = plugin.getArtDatabase().listArtists(UUID.randomUUID());
			artistsCount = artists.length;
			plugin.getLogger().info(MessageFormat.format("Async load of {0} artists started. {1} retrieved from disk cache.", artists.length, textureCache.size()));
			// skip the first one since we dummied it
			for (int i = 1; i < artists.length; i++) {
				//check cache
				if(this.isHeadCached(artists[i])) {
					cached++;
				} else {
					//Update the cache
					HeadCacheResponeType response = this.updateTexture(artists[i]);
					switch (response) {
						case MOJANG_API:
							mojang++;
							Thread.sleep(plugin.getConfiguration().HEAD_PREFETCH_PERIOD); //go real slow
							break;
						case CACHE:
							// cached is counted above to prevent unnecessary loading
							break;
						case NONE:
							failed++;
							if(plugin.getConfiguration().HEAD_FETCH_MOJANG) {
								//go slow if we failed an api call
								Thread.sleep(plugin.getConfiguration().HEAD_PREFETCH_PERIOD); 
							}
							break;
						case SERVER:
							server++;
							break;
						default:
							break;

					}
				}
			}
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Exception during prefetch!",e);
		}
		if((cached+mojang) == 0 && artistsCount>1) {
			plugin.getLogger().warning("Could not preload any player heads! Is the server in offline mode and not behind a Bungeecord?");
		} else {
			plugin.getLogger().info(MessageFormat.format("Loaded {0} from disk cache, {1} from server, and {2} from mojang out of {3} artists with {4} failures", cached, server, mojang, artistsCount - 1, failed));
			if(cached+mojang < artistsCount) {
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
	protected ItemStack getHead(UUID playerId) throws HeadFetchException {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		Optional<SkullMeta> meta = getHeadMeta(playerId);
		if (!meta.isPresent()) { //try loading it the normal way
			SkullMeta headmeta = (SkullMeta) head.getItemMeta();
			OfflinePlayer player = ArtMap.instance().getServer().getOfflinePlayer(playerId);
			if(player.hasPlayedBefore()) {
				headmeta.setOwningPlayer(player);
				headmeta.setDisplayName(player.getName());
				head.setItemMeta(headmeta);
			}
			return head; 
		}
		head.setItemMeta(meta.get());
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
	 * Retrieve the name of the player from the cache.
	 * @param playerId The id of the player to lookup.
	 * @return The name of the player or null if it wasn't cached.
	 */
	public String getPlayerName(UUID playerId) {
		if(textureCache.containsKey(playerId)) {
			return textureCache.get(playerId).name;
		}
		return null;
	}

	/**
	 * Search the cache for a artist name that matches the search term.
	 * @param term The search term.
	 * @return An array of matching names and an empty array if none or found.
	 */
	public String[] searchCache(String term) {
		return nameToUUID.keySet().stream().filter( name -> name.contains(term)).toArray(String[]::new);
	}

	/**
	 * Retrieve the player id for the given name from the cache.
	 * @param playername The playername to get the ID of.
	 * @return Optionally the player id if cached.
	 */
	public Optional<UUID> getPlayerUUID(String playername) {
		return Optional.ofNullable(nameToUUID.get(playername));
	}

	/**
	 * Create a player skullMeta for the provided player id.
	 * 
	 * @param playerId The ID of the player to get the skull meta for.
	 * @return The Skull meta.
	 * @throws HeadFetchException
	 */
	public Optional<SkullMeta> getHeadMeta(UUID playerId) throws HeadFetchException {
		// is it in the cache?
		if (!textureCache.containsKey(playerId)) {
			this.updateTexture(playerId);
		}
		TextureData data = textureCache.get(playerId);
		if (data == null) {
			return Optional.empty();
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

		return Optional.of((SkullMeta) headMeta);
	}

	protected HeadCacheResponeType updateTexture(UUID playerId) throws HeadFetchException {
		//Try and get the head texture from the server
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		OfflinePlayer player = ArtMap.instance().getServer().getOfflinePlayer(playerId);
		//Dont try from the server if they havent been on it recently
		if(player.hasPlayedBefore()) {
			meta.setOwningPlayer(player);
			meta.setDisplayName(player.getName());
			head.setItemMeta(meta);
			GameProfile profile = Reflections.getField(meta.getClass(), "profile", GameProfile.class).get(meta);
			PropertyMap propertyMap = profile.getProperties();
			//ArtMap.instance().getLogger().info("Here 1! " + propertyMap.keySet().stream().map(Object::toString).collect(Collectors.joining(",")));
			if(propertyMap != null && propertyMap.containsKey("textures")) {
				Optional<Property> prop = profile.getProperties().get("textures").stream().findFirst();
				//ArtMap.instance().getLogger().info("Here 2! " + player.getName());
				if(prop.isPresent()) {
					//ArtMap.instance().getLogger().info("Here 3! " + player.getName());
					TextureData data = new TextureData(player.getName(),prop.get().getValue());
					textureCache.put(playerId, data);
					this.saveCacheFile(cacheFile);
					nameToUUID.put(player.getName(), player.getUniqueId());
					return HeadCacheResponeType.SERVER;
				}
			}
		}
		//Use the mojang if the local server look up dies
		if(plugin.getConfiguration().HEAD_FETCH_MOJANG) {
			Optional<TextureData> data = getSkinUrl(playerId);
			if(data.isPresent()) {
				textureCache.put(playerId, data.get());
				this.saveCacheFile(cacheFile);
				nameToUUID.put(data.get().name, playerId);
				return HeadCacheResponeType.MOJANG_API;
			}
		}
		return HeadCacheResponeType.NONE;
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
			if(json == null) {
				throw new HeadFetchException("Skin texture could not be loaded! invalid uuid!");
			}
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
		} catch ( Throwable e ) {
			throw new HeadFetchException("Failure parsing skin texture json. You may ignore ths warning.");
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

	/** Where the skin was loaded from.
	 * Used by the prefetch to know when it needs to rate limit.
	 */
	public enum HeadCacheResponeType {
		/** Retrieved from Cache */
		CACHE, 
		/** Retrieved from Server */
		SERVER, 
		/** Retrieved from Mojang API */
		MOJANG_API, 
		/** Failure to get skin */
		NONE
	}

}
