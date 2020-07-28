package me.Fupery.ArtMap.Compatibility;

import io.netty.channel.Channel;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Compatability.ReflectionHandler;

import org.bukkit.entity.Player;

public class VanillaReflectionHandler implements ReflectionHandler {
    @Override
    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        Object nmsPlayer, playerConnection, networkManager;
        Channel channel;
        nmsPlayer = ArtMap.instance().getReflection().invokeMethod(player, "getHandle");
        playerConnection = ArtMap.instance().getReflection().getField(nmsPlayer, "playerConnection");
        networkManager = ArtMap.instance().getReflection().getField(playerConnection, "networkManager");
        channel = (Channel) ArtMap.instance().getReflection().getField(networkManager, "channel");
        return channel;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}
