package me.Fupery.ArtMap.IO.Protocol.In;

import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class GenericPacketReceiver extends PacketReceiver {

    private final Map<UUID, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private static final String handlerName = "ArtMapHandler";

    @Override
    public void injectPlayer(Player player) throws ReflectiveOperationException {
        Channel channel = getChannel(player);
        PacketHandler handler;
        try {
            handler = (PacketHandler) channel.pipeline().get(handlerName);

            if (handler == null) {
                handler = new PacketHandler();
                channel.pipeline().addBefore("packet_handler", handlerName, handler);
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            handler = (PacketHandler) channel.pipeline().get(handlerName);
        }
        handler.player = player;
    }

    @Override
    public void uninjectPlayer(Player player) {

        try {
            final Channel channel = getChannel(player);

            if (channel.pipeline().get(handlerName) != null) {

                channel.eventLoop().execute(() -> {
                    if (channel.pipeline().get(handlerName) != null) {
                        channel.pipeline().remove(handlerName);
                    }
                });
            }
        } catch (Exception e) {
            ArtMap.instance().getLogger().log(Level.SEVERE,"Error unbinding player channel!",e);
        }
        channelLookup.remove(player.getUniqueId());
    }

    @Override
    public void close() {

        if (channelLookup != null && channelLookup.size() > 0) {

            for (UUID player : channelLookup.keySet()) {
                uninjectPlayer(Bukkit.getPlayer(player));
            }
            channelLookup.clear();
        }
    }

    private Channel getChannel(Player player) throws ReflectiveOperationException {
        Channel channel = channelLookup.get(player.getUniqueId());

        if (channel == null) {
            channel = ArtMap.instance().getReflection().getPlayerChannel(player);

            if (channel == null) {
                Bukkit.getLogger().warning(Lang.PREFIX + "Error binding player channel!");
                return null;
            }
            channelLookup.put(player.getUniqueId(), channel);
        }

        return channel;
    }

    private Object onPacketInAsync(Player player, Channel channel, Object packet) {
        if (!ArtMap.instance().getArtistHandler().containsPlayer(player)) return packet;
        return ArtMap.instance().getArtistHandler().handlePacket(player, ArtMap.instance().getReflection().getArtistPacket(packet)) ? packet : null;
    }

    private final class PacketHandler extends ChannelDuplexHandler {
        private Player player;

        @Override
        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {

            final Channel channel = context.channel();
            msg = onPacketInAsync(player, channel, msg);
            if (msg != null) {
                super.channelRead(context, msg);
            }
        }
    }
}
