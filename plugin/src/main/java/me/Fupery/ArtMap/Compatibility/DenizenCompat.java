package me.Fupery.ArtMap.Compatibility;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatibility.Dipenizen.ArtMapArt;
import me.Fupery.ArtMap.Compatibility.Dipenizen.ArtMapArtist;
import me.Fupery.ArtMap.Compatibility.Dipenizen.ArtMapArtists;
import me.Fupery.ArtMap.api.Compatability.ReflectionHandler;
import net.aufdemrand.denizencore.objects.ObjectFetcher;

public class DenizenCompat implements ReflectionHandler {

    private boolean loaded = false;

    DenizenCompat() {
        Plugin plugin = ArtMap.instance().getServer().getPluginManager().getPlugin("Denizen");
        loaded = (plugin != null && plugin.isEnabled());
        if(!loaded){
            return;
        }
        // Add denizen objects for use in scripts
        Plugin dipenizen = ArtMap.instance().getServer().getPluginManager().getPlugin("Dipenizen");
        if(dipenizen != null && dipenizen.isEnabled()) {
            ObjectFetcher.registerWithObjectFetcher(ArtMapArt.class);
            ObjectFetcher.registerWithObjectFetcher(ArtMapArtist.class);
            ObjectFetcher.registerWithObjectFetcher(ArtMapArtists.class);
        }
    }

    //don't ask.
    private static Object getSuperSuperField(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field;
        try {
            field = obj.getClass().getSuperclass().getSuperclass().getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new NoSuchFieldException(String.format("Field '%s' could not be found in '%s'. Fields found: {%s}",
                    fieldName, obj.getClass().getName(), Arrays.asList(obj.getClass().getDeclaredFields())));
        }
        return field.get(obj);
    }

    @Override
    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        Object nmsPlayer, denizenPacketListener, networkManager;
        Channel channel;
        
        nmsPlayer = ArtMap.instance().getReflection().invokeMethod(player, "getHandle");
        denizenPacketListener = ArtMap.instance().getReflection().getField(nmsPlayer, "playerConnection");
        networkManager = getSuperSuperField(denizenPacketListener, "networkManager");
        channel = (Channel) ArtMap.instance().getReflection().getSuperField(networkManager, "channel");

        return channel;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
