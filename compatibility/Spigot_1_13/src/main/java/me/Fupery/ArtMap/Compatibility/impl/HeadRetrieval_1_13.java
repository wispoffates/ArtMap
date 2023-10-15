package me.Fupery.ArtMap.Compatibility.impl;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
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
        //ArtMap.instance().getLogger().info("Here 1! " + propertyMap.keySet().stream().map(Object::toString).collect(Collectors.joining(",")));
        if(propertyMap != null && propertyMap.containsKey("textures")) {
            Optional<Property> prop = profile.getProperties().get("textures").stream().findFirst();
            //ArtMap.instance().getLogger().info("Here 2! " + player.getName());
            if(prop.isPresent()) {
                //ArtMap.instance().getLogger().info("Here 3! " + player.getName());
                return Optional.of(new TextureData(player.getName(),prop.get().getValue()));
            }
        }
        return Optional.empty();
    }
    
}
