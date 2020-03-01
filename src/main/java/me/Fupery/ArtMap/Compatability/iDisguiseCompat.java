package me.Fupery.ArtMap.Compatability;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Utils.Reflection;

public class iDisguiseCompat implements ReflectionHandler {

    private boolean loaded = false;

    public iDisguiseCompat() {
        Plugin plugin = ArtMap.instance().getServer().getPluginManager().getPlugin("iDisguise");
        loaded = (plugin != null && plugin.isEnabled());
    }

    @Override
    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        Object nmsPlayer, playerConnection, networkManager;
        Channel channel;
        nmsPlayer = Reflection.invokeMethod(player, "getHandle");
        playerConnection = Reflection.getField(nmsPlayer, "playerConnection");
        networkManager = Reflection.getSuperField(playerConnection, "networkManager");
        channel = (Channel) Reflection.getField(networkManager, "channel");
        return channel;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
