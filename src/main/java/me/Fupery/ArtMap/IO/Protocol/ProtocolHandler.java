package me.Fupery.ArtMap.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.In.GenericPacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.PacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.ProtocolLibReceiver;
;

public class ProtocolHandler {

    public final PacketReceiver PACKET_RECIEVER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtMap.instance().getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
        } catch (Exception | NoClassDefFoundError e) {
            useProtocolLib = false;
        }
        if (useProtocolLib) {
            PACKET_RECIEVER = new ProtocolLibReceiver();
            Bukkit.getLogger().info("[ArtMap] ProtocolLib hooks enabled.");
        } else {
            PACKET_RECIEVER = new GenericPacketReceiver();
        }
    }
}
