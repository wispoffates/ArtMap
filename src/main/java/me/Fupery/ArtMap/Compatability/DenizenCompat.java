package me.Fupery.ArtMap.Compatability;

import java.lang.reflect.Field;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.Compatability.Dipenizen.ArtMapArt;
import me.Fupery.ArtMap.Compatability.Dipenizen.ArtMapArtist;
import me.Fupery.ArtMap.Compatability.Dipenizen.ArtMapArtists;
import me.Fupery.ArtMap.Utils.Reflection;
import net.aufdemrand.denizencore.objects.ObjectFetcher;

public class DenizenCompat implements ReflectionHandler {

    private final boolean loaded;

    DenizenCompat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Denizen");
        loaded = (plugin != null && plugin.isEnabled());
		// Add denizen objects for use in scripts
		if (loaded) {
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

        nmsPlayer = Reflection.invokeMethod(player, "getHandle");
        denizenPacketListener = Reflection.getField(nmsPlayer, "playerConnection");
        networkManager = getSuperSuperField(denizenPacketListener, "networkManager");
        channel = (Channel) Reflection.getSuperField(networkManager, "channel");

        return channel;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
