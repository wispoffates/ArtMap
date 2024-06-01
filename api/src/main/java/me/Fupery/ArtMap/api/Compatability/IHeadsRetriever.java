package me.Fupery.ArtMap.api.Compatability;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import me.Fupery.ArtMap.api.Exception.HeadFetchException;

public interface IHeadsRetriever {
    
    public Optional<TextureData> getTextureData(OfflinePlayer player);
	public Optional<SkullMeta> getHeadMeta(UUID playerId, TextureData textureData) throws HeadFetchException;

    public static class TextureData {
		public String	name;
		public String	texture;
		public HeadCacheType type = HeadCacheType.PROFILE;

		public TextureData(String name, String texture, HeadCacheType type) {
			this.name = name;
			this.texture = texture;
			this.type = type;
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

	public enum HeadCacheType {
		PROFILE,
		URL
	}
}
