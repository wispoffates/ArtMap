package me.Fupery.ArtMap.Heads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.Fupery.ArtMap.ArtMap;

/**
 * Heads handler to be used with caching head textures.
 * 
 * @author wispoffates
 */
public class Heads {

	static private JsonParser				parser				= new JsonParser();
	static private String					API_PROFILE_LINK	= "https://sessionserver.mojang.com/session/minecraft/profile/";

	private static Map<UUID, TextureData>	textureCache		= new HashMap<>();

	/**
	 * Create a head item with the provided texture.
	 * 
	 * @param playerId The ID of the player get the skull for.
	 * 
	 * @return The Skull.
	 */
	public static ItemStack getHead(UUID playerId) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = getHeadMeta(playerId);
		if (meta == null) {
			return null;
		}
		head.setItemMeta(meta);
		return head;
	}

	/**
	 * Create a player skullMeta for the provided player id.
	 * 
	 * @param playerId The ID of the player to get the skull meta for.
	 * @return The Skull meta.
	 */
	public static SkullMeta getHeadMeta(UUID playerId) {
		// is it in the cache?
		if (!textureCache.containsKey(playerId)) {
			TextureData data = getSkinUrl(playerId);
			if (data == null) {
				return null;
			}
			textureCache.put(playerId, data);
		}
		TextureData data = textureCache.get(playerId);
		if (data == null) {
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
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
		headMeta.setDisplayName(data.name);

		return (SkullMeta) headMeta;
	}

	/*
	 * HTTP Methods
	 */
	private static String getContent(String link) {
		try {
			URL url = new URL(link);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				return inputLine;
			}
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			ArtMap.instance().getLogger().info("Error retrieving head texture.  Server is likely over API limit temporarily.");
		}
		return null;
	}

	private static TextureData getSkinUrl(UUID uuid) {
		try {
			String id = uuid.toString().replaceAll("\\-", "");
			String json = getContent(API_PROFILE_LINK + id);
			JsonObject o = parser.parse(json).getAsJsonObject();
			String name = o.get("name").getAsString();
			String jsonBase64 = o.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
			/*
			 * o = parser.parse(new
			 * String(Base64.decodeBase64(jsonBase64))).getAsJsonObject(); if
			 * (o.get("textures").getAsJsonObject().size() == 0) { // handle players that
			 * have empty skin textures return new TextureData(name, ""); } else { String
			 * skinUrl =
			 * o.get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").
			 * getAsString(); return new TextureData(name, skinUrl); }
			 */
			return new TextureData(name, jsonBase64);
		} catch (Exception e) {
			//ArtMap.instance().getLogger().info("Failed to parse JSON for user id:" + uuid);
		}
		return null;
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
