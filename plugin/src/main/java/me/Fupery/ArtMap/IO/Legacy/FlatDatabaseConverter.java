package me.Fupery.ArtMap.IO.Legacy;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandExport.ArtworkExport;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class FlatDatabaseConverter extends DatabaseConverter {

    private JavaPlugin plugin;

    public FlatDatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isNeeded() {
        String dbFileName = "mapList.yml";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        return databaseFile.exists();
    }

    @Override
    public boolean canBeForced() {
        String dbFileName = "mapList.yml.off";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        return databaseFile.exists();
    }

    @Override
    public boolean createExport(boolean force) throws Exception {
        String dbFileName = "mapList.yml";
        if (force) {
            dbFileName = "mapList.yml.off";
        }
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists())
            return false;
        sendMessage("Old 'mapList.yml' database found! Converting to new format ...");
        sendMessage("(This may take a while, but only needs to run once)");
        List<ArtworkExport> artList = readArtworks(databaseFile);
        String msg = this.export(artList);
        sendMessage(msg);

        File disabledDatabaseFile = new File(plugin.getDataFolder(), dbFileName + ".off");
        if (!databaseFile.renameTo(disabledDatabaseFile)) {
            sendMessage("Error disabling mapList.yml! Delete this file manually.");
            return false;
        }
        return true;
    }

    private List<ArtworkExport> readArtworks(File databaseFile) {
        List<ArtworkExport> artList = new ArrayList<>();
        FileConfiguration database = YamlConfiguration.loadConfiguration(databaseFile);
        ConfigurationSection artworks = database.getConfigurationSection("artworks");

        if (artworks == null)
            return artList;

        for (String title : artworks.getKeys(false)) {
            ConfigurationSection map = artworks.getConfigurationSection(title);
            if (map != null) {
                int mapIDValue = map.getInt("mapID");
                OfflinePlayer player = (map.contains("artist"))
                        ? Bukkit.getOfflinePlayer(UUID.fromString(map.getString("artist")))
                        : null;
                String date = map.getString("date");
                MapView mapView = ArtMap.getMap(mapIDValue);
                if (mapView == null) {
                    sendMessage(String.format("    Ignoring '%s' (failed to access map data) ...", title));
                    continue;
                }
                MapArt artwork = new MapArt(mapIDValue, title, player.getUniqueId(), player.getName(), date);
                try {
                    if (ArtMap.instance().getArtDatabase().containsArtwork(artwork, true)) {
                        sendMessage(String.format("    Ignoring '%s' (already exists in database) ...", title));
                    } else {
                        sendMessage(String.format("    Converting '%s' ...", title));
                        try {
                            artList.add(new ArtworkExport(artwork, new Map(mapView).compress()));
                        } catch (IOException e) {
                            sendMessage(String.format("    Failure converting '%s'!!!", title));
                            ArtMap.instance().getLogger().log(Level.SEVERE, "Failure converting: " + title, e);
                        }
                    }
                } catch (SQLException e) {
                    sendMessage("Error accessing DB!");
                    ArtMap.instance().getLogger().log(Level.SEVERE,"Error accessing DB!",e);
                }
            }
        }
        return artList;
    }
}
