package me.Fupery.ArtMap.Command;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Command.CommandExport.ArtworkExport;
import me.Fupery.ArtMap.api.Config.Lang;

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
            return;
        }

        File importFile = new File(ArtMap.instance().getDataFolder(), importFilename);
        if(!importFile.exists()) {
            importFilename+=".json";
        }
        //add .json and look again
        importFile = new File(ArtMap.instance().getDataFolder(), importFilename);
        if (!importFile.exists()) {
            sender.sendMessage("Import file cannot be found!");
            return;
        }
        try {
            FileReader reader = new FileReader(importFile);
            Gson gson = ArtMap.instance().getGson(true);
            Type collectionType = new TypeToken<List<ArtworkExport>>() {
            }.getType();
            artToImport = gson.fromJson(reader, collectionType);
            reader.close();
        } catch (IOException e) {
            ArtMap.instance().getLogger().log(Level.SEVERE, "Failure reading import!", e);
        }

        sender.sendMessage(MessageFormat.format("{0} artworks available for import.", artToImport.size()));
        switch (args[1]) {
        case "-all":
            this.delayedImport(sender,artToImport);
            break;
        case "-artist":
            if (args.length < 4) {
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
            }
            List<ArtworkExport> byArtist = new LinkedList<>();
            try {
                UUID id = UUID.fromString(args[2]);
                byArtist = artToImport.stream().filter(art -> {
                    return art.getArtist().equals(id);
                }).collect(Collectors.toList());
            } catch (Exception exp) {
                // its a name then
                Player p = Bukkit.getPlayer(args[2]);
                if(p==null) {
                    if(sender != null) {
                        sender.sendMessage("Player not found! :: " + args[2]);
                        return;
                    }
                }
                byArtist = artToImport.stream().filter(art -> {
                    return art.getArtist().equals(p.getUniqueId());
                }).collect(Collectors.toList());
            }
            this.delayedImport(sender,byArtist);
            break;
        case "-title":
            if (args.length < 4) {
                // TODO: need usage
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
            }
            String title = args[2];
            List<ArtworkExport> byTitle = artToImport.stream().filter(art -> {
                return art.getTitle().equals(title);
            }).collect(Collectors.toList());
            this.delayedImport(sender,byTitle);
            break;
        default:
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
            return;
        }
        sender.sendMessage("Import complete. Check the console for errors.");
    }

    //Slows the import down a bit to keep from lagging the main thread (10 per second)
    private void delayedImport(CommandSender sender,List<ArtworkExport> arts) {
        long delayConfig = ArtMap.instance().getConfig().getInt("importDelay");
        ArtMap.instance().getLogger().info("Delay="+delayConfig);
        //0 if not set so move to 100 default
        final long delay = delayConfig<=0 ? 100 : delayConfig;
        Bukkit.getScheduler().runTaskAsynchronously(ArtMap.instance(), () -> {
            arts.forEach(art -> {
                try {
                    art.importArtwork(sender);
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    // don't care
                }
            });
        });
    }
}
