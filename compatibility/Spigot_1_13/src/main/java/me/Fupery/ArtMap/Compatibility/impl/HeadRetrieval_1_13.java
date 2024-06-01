package me.Fupery.ArtMap.Compatibility.impl;

import java.util.Optional;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import me.Fupery.ArtMap.api.Compatability.IHeadsRetriever;
import me.Fupery.ArtMap.api.Utils.Reflections;

public class HeadRetrieval_1_13 implements IHeadsRetriever {

    @Override
    public Optional<TextureData> getTextureData(OfflinePlayer player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwningPlayer(player);
        meta.setDisplayName(player.getName());
        head.setItemMeta(meta);
        GameProfile profile = Reflections.getField(meta.getClass(), "profile", GameProfile.class).get(meta);
        PropertyMap propertyMap = profile.getProperties();
        if(propertyMap != null && propertyMap.containsKey("textures")) {
            Optional<Property> prop = profile.getProperties().get("textures").stream().findFirst();
            if(prop.isPresent()) {
                return Optional.of(new TextureData(player.getName(),prop.get().getValue(),HeadCacheType.PROFILE));
            }
        }
        return Optional.empty();
    }

    @Override
	public Optional<SkullMeta> getHeadMeta(UUID playerId, TextureData textureData) {
		if(textureData == null) {
			return Optional.empty();
		}
		GameProfile profile = new GameProfile(playerId, textureData.name);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		// handle players without skin textures
		if (!textureData.texture.isEmpty()) {
			propertyMap.put("textures", new Property("textures", textureData.texture));
		}
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		ItemMeta headMeta = head.getItemMeta();
		Class<?> headMetaClass = headMeta.getClass();
		Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
		headMeta.setDisplayName(textureData.name);

		return Optional.of((SkullMeta) headMeta);
	}
    
}
