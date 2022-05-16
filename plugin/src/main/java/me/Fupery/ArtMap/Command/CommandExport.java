package me.Fupery.ArtMap.Command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Exception.DuplicateArtworkException;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;

public class CommandExport extends AsyncCommand {

    CommandExport() {
        super(null, "/art export <-all|-artist|-title> [name] <output_file_name>.json", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        if (!sender.hasPermission("artmap.admin")) {
            msg.message = Lang.NO_PERM.get();
            return;
        }

        // args[0] is export
        if (args.length < 3) {
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
            return;
        }

        List<MapArt> artToExport = new LinkedList<>();
        String exportFilename = null;

        switch (args[1]) {
            case "-all":
                try {
                    for (UUID artist : ArtMap.instance().getArtDatabase().listArtists()) {
                        artToExport.addAll(Arrays.asList(ArtMap.instance().getArtDatabase().listMapArt(artist)));
                    }
                } catch (SQLException e2) {
                    msg.message = "Database error! Check logs.";
                    ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e2);
                    return;
                }
                exportFilename = args[2];

                break;
            case "-artist":
                if (args.length < 4) {
                    // TODO: need usage
                    msg.message = Lang.COMMAND_EXPORT.get();
                    return;
                }
                MapArt[] arts;
                try {
                    UUID id = UUID.fromString(args[2]);
                    arts = ArtMap.instance().getArtDatabase().listMapArt(id);
                } catch (Exception exp) {
                    // its a name then
                    try {
                        Player p = Bukkit.getPlayer(args[2]);
                        if(p==null) {
                            if(sender != null) {
                                sender.sendMessage("Player not found! :: " + args[2]);
                                return;
                            }
                        }
                        arts = ArtMap.instance().getArtDatabase().listMapArt(p.getUniqueId());
                    } catch (SQLException e) {
                        msg.message = "Database error! Check logs.";
                        ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
                        return;
                    }
                }
                if (arts != null) {
                    artToExport.addAll(Arrays.asList(arts));
                    exportFilename = args[3];
                } else {
                    msg.message = String.format(Lang.NO_ARTWORKS.get(), args[2]);
                    return;
                }
                break;
            case "-title":
                if (args.length < 4) {
                    // TODO: need usage
                    msg.message = Lang.COMMAND_EXPORT.get();
                    return;
                }
                Optional<MapArt> art;
                try {
                    art = ArtMap.instance().getArtDatabase().getArtwork(args[2]);
                } catch (SQLException e1) {
                    msg.message = "Database error! Check logs.";
                    ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e1);
                    return;
                }
                if (art.isPresent()) {
                    artToExport.add(art.get());
                    exportFilename = args[3];
                } else {
                    msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[2]);
                    return;
                }
                break;
            default:
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
        }

        if (artToExport.isEmpty()) {
            sender.sendMessage("No artwork found export!");
            return;
        }
        sender.sendMessage(MessageFormat.format("Beginning export of {0} artworks.", artToExport.size()));
        List<ArtworkExport> exports = new LinkedList<>();
        for (MapArt artwork : artToExport) {
            CompressedMap map = null;
            try {
                map = ArtMap.instance().getArtDatabase().getArtworkCompressedMap(artwork.getMapId());
            } catch (SQLException e) {
                msg.message = "Database error! Check logs.";
                ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
            }
            if (map != null) {
                exports.add(new ArtworkExport(artwork, map));
            } else {
                sender.sendMessage(artwork + " no matching map in Map table! Error! Skipping...");
            }
        }
        if(!exportFilename.endsWith(".json")) {
            exportFilename = exportFilename + ".json";
        }
        File exportFile = new File(ArtMap.instance().getDataFolder(), exportFilename);
        if (exportFile.exists()) {
            sender.sendMessage("File all ready exists please choose another filename.");
            return;
        }
        try (FileWriter writer = new FileWriter(exportFile);) {
            Gson gson = ArtMap.instance().getGson(true);
            Type collectionType = new TypeToken<List<ArtworkExport>>() {
            }.getType();
            gson.toJson(exports, collectionType, writer);
            writer.flush();
            sender.sendMessage(MessageFormat.format("Completed export of {0} artworks.", exports.size()));
        } catch (IOException e) {
            ArtMap.instance().getLogger().log(Level.SEVERE, "Failure writing export!", e);
        }
    }

    /**
     * Class to gson export and import ArtMap data. Purposefully exludes mapId as it
     * will need to be given a new id on the import side.
     */
    public static class ArtworkExport {
        private String title;
        private UUID artist;
        private String date;
        // base64 encoded map data
        private String mapData;
        private Integer hash;

        protected ArtworkExport() {
            // GSON Constructor
        }

        /**
         * Constructor
         * 
         * @param artwork The artwork to export.
         * @param map     The compressedMap to export.
         */
        public ArtworkExport(MapArt artwork, CompressedMap map) {
            this.title = artwork.getTitle();
            this.artist = artwork.getArtist();
            this.date = artwork.getDate();

            this.hash = map.getHash();
            this.mapData = Base64.getEncoder().encodeToString(map.getCompressedMap());
        }

        /**
         * Import this artwork in the database.
         * 
         */
        public void importArtwork(CommandSender sender) {
            // 1.14 requires create map to be run on the main thread!
            ArtMap.instance().getScheduler().SYNC.run(() -> {
                try {
                    Map map = ArtMap.instance().getArtDatabase().createMap();
                    CompressedMap cMap = new CompressedMap(map.getMapId(), this.hash,
                            Base64.getDecoder().decode(this.mapData));
                    map.setMap(cMap.decompressMap(), true);
                    Optional<MapArt> check = ArtMap.instance().getArtDatabase().getArtwork(this.title);
                    if (check.isPresent()) {
                        // art with this title all ready exists see if its the same artwork (artist,and
                        // hash) otherwise increment name by 1
                        if (check.get().getArtist().equals(this.artist)
                                && check.get().getMap().compress().getHash().equals(this.hash)) {
                            throw new DuplicateArtworkException("Artwok all ready in database");
                        }
                        this.title = this.title + "_1";
                    }
                    // null artistname since its dropped when importing into the database.
                    MapArt mapArt = new MapArt(map.getMapId(), this.title, this.artist, null, this.date);
                    ArtMap.instance().getArtDatabase().saveArtwork(mapArt, cMap);
                    ArtMap.instance().getLogger().info(this.title + " :: Import Successful!");
                } catch (DuplicateArtworkException dae) {
                    if (sender != null) {
                        ArtMap.instance().getLogger().warning(this.title + " :: Import Failed! :: " + dae.getMessage());
                    }
                } catch (Exception e) {
                    if (sender != null) {
                        ArtMap.instance().getLogger().warning(this.title + " :: Import Failed! :: " + e.getMessage());
                    }
                    ArtMap.instance().getLogger().log(Level.SEVERE,
                            this.title + " :: Import Failed! :: " + e.getMessage(), e);
                }
            });
        }

        @Override
        public String toString() {
            return MessageFormat.format("{0} by {1} created on {2}", this.title, this.artist, this.date);
        }

        public UUID getArtist() {
            return this.artist;
        }

        public String getTitle() {
            return this.title;
        }

    }
}
