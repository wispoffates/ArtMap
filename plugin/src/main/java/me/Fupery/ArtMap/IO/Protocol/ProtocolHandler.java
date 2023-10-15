package me.Fupery.ArtMap.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.Protocol.In.PacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.ProtocolLibReceiver;
;

public class ProtocolHandler {

    public final PacketReceiver PACKET_RECIEVER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtMap.instance().getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
            PACKET_RECIEVER = new ProtocolLibReceiver();
            Bukkit.getLogger().info("[ArtMap] Using ProtocolLib PacketReciever.");
        } catch (Exception | NoClassDefFoundError e) {
            throw new RuntimeException("ProtocolLib could not be hooked! Please install a compatible version of ProtocolLib.");
        }
    }
}
