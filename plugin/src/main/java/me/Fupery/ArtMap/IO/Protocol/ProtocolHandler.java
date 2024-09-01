package me.Fupery.ArtMap.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.IO.Protocol.In.PacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.ProtocolLibReceiver;
import me.Fupery.ArtMap.api.Exception.ArtMapException;


public class ProtocolHandler {

    private final PacketReceiver packetReceiver;

    public ProtocolHandler() throws ArtMapException {
        try {
            ProtocolLibrary.getProtocolManager();
            packetReceiver = new ProtocolLibReceiver();
            Bukkit.getLogger().info("[ArtMap] Using ProtocolLib PacketReciever.");
        } catch (Exception | NoClassDefFoundError e) {
            throw new ArtMapException("ProtocolLib could not be hooked! Please install a compatible version of ProtocolLib.");
        }
    }

    public PacketReceiver getPacketReceiver() {
        return this.packetReceiver;
    }
}
