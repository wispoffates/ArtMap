package me.Fupery.ArtMap.Command;

import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Map;

class CommandTest extends AsyncCommand {

    CommandTest() {
        super(null, "/art test <-create> <count>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        if (!sender.hasPermission("artmap.admin")) {
            msg.message = Lang.NO_PERM.get();
            return;
        }

        // args[0] is test
        if (args.length < 2) {
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
            return;
        }

        switch (args[1]) {
        case "-create":
            Bukkit.getScheduler().runTaskAsynchronously(ArtMap.instance(), ()->{
                int count = Integer.parseInt(args[2]);
                String series = String.valueOf(System.currentTimeMillis());
                for(int i=0;i<count;i++) {
                    try {
                        createArt(UUID.fromString("5dcadcf6-7070-42ab-aaf3-b60a120a6bcf"), "test_"+series+"_"+i, new Date().toString(),i%100==0);
                        //Thread.sleep(2); //slow it down just a bit
                    } catch(Exception e) {
                        System.out.println("Successfully created = " + (i-1));
                        break;
                    }
                }
            });

            break;
        default:
            // TODO: need usage
            msg.message = Lang.COMMAND_EXPORT.get();
        }
    }

    private void createArt(UUID artist, String name, String date, boolean print) throws Exception {
        Bukkit.getScheduler().runTask(ArtMap.instance(), ()->{
            try {
                Map map = ArtMap.instance().getArtDatabase().createMap();
                if(print) {
                    System.out.println("Created new Map: " + map.getMapId());
                }
                if(map.getMap() == null) {
                    System.out.println("Mapvies is null! :: " + map.getMapId());
                    throw new Exception("null mapaview!");
                }
                MapArt mapArt = new MapArt(map.getMapId(), name, artist, null, date);
                CompressedMap cMap = CompressedMap.compress(map.getMap());
                ArtMap.instance().getArtDatabase().saveArtwork(mapArt, cMap);
            } catch(Exception e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
            }
        });
    }

}
