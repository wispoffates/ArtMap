package me.Fupery.ArtMap.IO.Protocol.Out;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

public abstract class WrappedPacket<T> {

    protected final T rawPacket;

    public WrappedPacket(T packet) {
        this.rawPacket = packet;
    }

    public abstract void send(Player player);

    public static <T> WrappedPacket<T> raw(T packet, BiConsumer<Player, T> sendFunction) {
       return new WrappedPacket<T>(packet) {
           @Override
           public void send(Player player) {
               sendFunction.accept(player, packet);
           }
       };
    }
}
