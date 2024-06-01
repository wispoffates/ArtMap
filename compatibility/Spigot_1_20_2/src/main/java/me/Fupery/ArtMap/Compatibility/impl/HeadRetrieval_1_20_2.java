package me.Fupery.ArtMap.Compatibility.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import me.Fupery.ArtMap.api.Compatability.IHeadsRetriever;
import me.Fupery.ArtMap.api.Exception.HeadFetchException;

public class HeadRetrieval_1_20_2 implements IHeadsRetriever {

    @Override
    public Optional<TextureData> getTextureData(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        head.setItemMeta(meta);
        PlayerProfile profile = meta.getOwnerProfile();
        if(profile.getTextures() != null && profile.getTextures().isEmpty()) {
            PlayerTextures textures = profile.getTextures();
            return Optional.of(new TextureData(player.getName(),textures.getSkin().toString(),HeadCacheType.URL));
        }
        return Optional.empty();
    }

    @Override
	public Optional<SkullMeta> getHeadMeta(UUID playerId, TextureData textureData) throws HeadFetchException {
		if(textureData == null) {
			return Optional.empty();
		}
		PlayerProfile profile = Bukkit.createPlayerProfile(playerId, textureData.name);
        PlayerTextures textures = profile.getTextures();
        try {
            if(textureData.type == HeadCacheType.URL) {
                textures.setSkin(new URL(textureData.texture));
            } else {
                //old profile format lets do some magic to extract the url
                String json = new String(Base64.getDecoder().decode(textureData.texture.getBytes()));
                JsonElement root = JsonParser.parseString(json);
                String url = root.getAsJsonObject().get("textures").getAsJsonObject().get("SKIN").getAsJsonObject().get("url").getAsString();
                textures.setSkin(new URL(url));
            }
        } catch (MalformedURLException e) {
            throw new HeadFetchException("Failed to parse URL", e);
        } catch (NullPointerException| IllegalStateException e) {
            throw new HeadFetchException("Failed to parse json tp get skin url", e);
        }
		profile.setTextures(textures);
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwnerProfile(profile);
		meta.setDisplayName(textureData.name);

		return Optional.of(meta);
	}
    
}
