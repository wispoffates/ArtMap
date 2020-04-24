package me.Fupery.ArtMap.IO.Legacy;

import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandExport.ArtworkExport;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.Database.SQLiteDatabase;
import me.Fupery.ArtMap.IO.Database.SQLiteTable;
import me.Fupery.ArtMap.IO.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class V2DatabaseConverter extends DatabaseConverter {

    private JavaPlugin plugin;

    public V2DatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isNeeded() {
        String dbFileName = "ArtMap.db";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        return databaseFile.exists();
    }

    @Override
    public boolean canBeForced() {
        String dbFileName = "ArtMap.db.off";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        return databaseFile.exists();
    }

    @Override
    public boolean createExport(boolean force) throws Exception {
        String dbFileName = "ArtMap.db";
        if (force) {
            dbFileName = "ArtMap.db.off";
        }
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists())
            return false;

        sendMessage("Old 'ArtMap.db' database found! Converting to new format ...");
        sendMessage("(This may take a while, but only needs to run once)");

        List<ArtworkExport> artList = readArtworks(dbFileName);
        String message = this.export(artList);
        sendMessage(message);

        if (dbFileName.equals("ArtMap.db")) {
            if (!databaseFile.renameTo(new File(plugin.getDataFolder(), dbFileName + ".off"))) {
                sendMessage("Failed to move old ArtMap.db to ArtMap.db.off pleae do it manually.");
                return true;
            }
        }

        return true;
    }

    private List<ArtworkExport> readArtworks(String filename) throws SQLException {
        List<ArtworkExport> artList = new ArrayList<>();
        OldDatabase database = new OldDatabase(plugin, filename);
        OldDatabaseTable table = new OldDatabaseTable(database);
        database.initialize(table);
        for (RichMapArt artwork : table.readArtworks()) {
            String title = artwork.getArt().getTitle();
            sendMessage(String.format("    Converting '%s' ...", title));
            artList.add(new ArtworkExport(artwork.getArt(), artwork.getMap()));
        }
        return artList;
    }

    private static class RichMapArt {
        private final MapArt art;
        private final CompressedMap mapData;

        RichMapArt(MapArt art, CompressedMap mapData) {
            this.art = art;
            this.mapData = mapData;
        }

        public MapArt getArt() {
            return art;
        }

        public CompressedMap getMap() {
            return mapData;
        }
    }

    private static class OldDatabase extends SQLiteDatabase {

        OldDatabase(JavaPlugin plugin, String filename) {
            super(new File(plugin.getDataFolder(), filename));
        }

        private void initialize(OldDatabaseTable table) throws SQLException {
            super.initialize(table);
        }

        @Override
        protected Connection getConnection() {
            return super.getConnection();
        }
    }

    private static class OldDatabaseTable extends SQLiteTable {

        OldDatabaseTable(SQLiteDatabase database) {
            super(database, "artworks", "SELECT * FROM artworks");
        }

        List<RichMapArt> readArtworks() throws SQLException {
            return new QueuedQuery<List<RichMapArt>>() {

                protected void prepare(PreparedStatement statement) {
                }

                protected List<RichMapArt> read(ResultSet set) throws SQLException {
                    List<RichMapArt> artList = new ArrayList<>();
                    while (set.next()) {
                        try {
                            artList.add(readArtwork(set));
                        } catch (Exception e) {
                            ArtMap.instance().getLogger().log(Level.SEVERE, "Exception reading artwork!",e);
                        }
                    }
                    return artList;
                }
            }.execute("SELECT * FROM artworks");
        }

        private RichMapArt readArtwork(ResultSet set) throws SQLException, IOException {
            String title = set.getString("title");
            int id = set.getInt("id");
            UUID artist = UUID.fromString(set.getString("artist"));
            String date = set.getString("date");
            MapArt art = new MapArt(id, title, artist, Bukkit.getOfflinePlayer(artist).getName(), date);
            byte[] map = new f32x32().readBLOB(set.getBytes("map"));
            CompressedMap data = CompressedMap.compress(id, map);
            return new RichMapArt(art, data);
        }

        @Override
        protected void create() throws SQLException {
            new QueuedQuery<Boolean>() {
                @Override
                protected void prepare(PreparedStatement statement) {
                }

                @Override
                protected Boolean read(ResultSet set) throws SQLException {
                    return set.next();
                }
            }.execute("SELECT * FROM artworks LIMIT 1");
        }
    }
}
