package me.Fupery.ArtMap.Command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandExport.ArtworkExport;
import me.Fupery.ArtMap.Config.Lang;

class CommandImport extends AsyncCommand {

    CommandImport() {
        super(null, "/art import <-all|-artist|-title> [name] <import_file_name>.json", true);
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

        List<ArtworkExport> artToImport = new LinkedList<>();
        String importFilename = null;

        // get the import file name
        switch (args[1]) {
        case "-all":
            importFilename = args[2];
            break;
        case "-artist":
        case "-title":
            if (args.length < 4) {
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
            }
            importFilename = args[3];
            break;
        default:
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
        }

        File importFile = new File(ArtMap.instance().getDataFolder(), importFilename + ".json");
        if (!importFile.exists()) {
            sender.sendMessage("Import file cannot be found!");
            return;
        }
        try {
            FileReader reader = new FileReader(importFile);
            Gson gson = ArtMap.getGson(true);
            Type collectionType = new TypeToken<List<ArtworkExport>>() {
            }.getType();
            artToImport = gson.fromJson(reader, collectionType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(MessageFormat.format("{0} artworks available for import.", artToImport.size()));
        switch (args[1]) {
        case "-all":
            for (ArtworkExport artImport : artToImport) {
                try {
                    artImport.importArtwork();
                    sender.sendMessage("Successfully imported: " + artImport.toString());
                } catch (Exception e) {
                    sender.sendMessage("Failed imported: " + e.getMessage());
                }
            }
            break;
        case "-artist":
            if (args.length < 4) {
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
            }
            UUID id = Bukkit.getPlayer(args[2]).getUniqueId();
            artToImport.stream().filter(art -> {
                return art.getArtist().equals(id);
            }).forEach(art -> {
                try {
                    art.importArtwork();
                    sender.sendMessage("Successfully imported: " + art.toString());
                } catch (Exception e) {
                    sender.sendMessage("Failed imported: " + e.getMessage());
                }
            });
            break;
        case "-title":
            if (args.length < 4) {
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
            }
            String title = args[2];
            artToImport.stream().filter(art -> {
                return art.getTitle().equals(title);
            }).forEach(art -> {
                try {
                    art.importArtwork();
                    sender.sendMessage("Successfully imported: " + art.toString());
                } catch (Exception e) {
                    sender.sendMessage("Failed imported: " + e.getMessage());
                }
            });
            break;
        default:
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
        }
        sender.sendMessage("Import complete.");
    }
}
