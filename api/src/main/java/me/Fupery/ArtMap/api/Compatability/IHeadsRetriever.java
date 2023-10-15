package me.Fupery.ArtMap.api.Compatability;

import java.util.Optional;

import org.bukkit.OfflinePlayer;

public interface IHeadsRetriever {
    
    public Optional<TextureData> getTextureData(OfflinePlayer player);

    public static class TextureData {
		public String	name;
		public String	texture;

		public TextureData(String name, String texture) {
			this.name = name;
			this.texture = texture;
		}
	}
}
