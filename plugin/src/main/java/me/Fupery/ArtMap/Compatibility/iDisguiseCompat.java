package me.Fupery.ArtMap.Compatibility;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Compatability.ReflectionHandler;

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
        nmsPlayer = ArtMap.instance().getReflection().invokeMethod(player, "getHandle");
        playerConnection = ArtMap.instance().getReflection().getField(nmsPlayer, "playerConnection");
        networkManager = ArtMap.instance().getReflection().getSuperField(playerConnection, "networkManager");
        channel = (Channel) ArtMap.instance().getReflection().getField(networkManager, "channel");
        return channel;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
