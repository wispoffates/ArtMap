package me.Fupery.ArtMap.Command;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;

public class Repair extends AsyncCommand {

    Repair() {
        super(null, "/art repair <-scan|-repair> [name|ID|-all]", true);
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
            msg.message = Lang.REPAIR_USAGE.get();
            return;
        }

        boolean all = "-all".equalsIgnoreCase(args[2]);
        
        switch (args[1]) {
            case "-scan":
                if(all) {
                    msg.message = "Scan will be run asynchronously check the logs for progress.";
                    repairAll(false);
                    return;
                }
                msg.message = repair(args[2], false);
                break;
            case "-repair":
                if(all) {
                    msg.message = "Scan will be run asynchronously check the logs for progress.";
                    repairAll(true);
                    return;  
                }
                msg.message = repair(args[2], true);
                break;
            default:
                msg.message = Lang.COMMAND_EXPORT.get();
                return;
        }

    }


    private String repair(String input, boolean repair) {
        Integer id = null;
        try {
            id = Integer.parseInt(input);
        } catch(NumberFormatException nfe) {
            //likely an artwork name
            Optional<MapArt> art;
            try {
                art = ArtMap.instance().getArtDatabase().getArtwork(input);
            } catch (SQLException e) {
                return e.getMessage();
            }
            if(!art.isPresent()) {
                return "No artwork found with this name: " + input;
            }
            id = art.get().getMapId();
        }
        try {
            boolean repaired = this.repairArtwork(id, repair);
            if(repair) {
                return repaired ? "Artwork repaired." : "Artwork repair failed check the logs.";
            } else {
                return repaired ? "Artwork is corrupted." : "Artwork is in a good state.";
            }
            
        } catch (FileNotFoundException e) {
            return e.getMessage();
        }
        
    }

    private void repairAll(boolean repair) {
        ArtMap.instance().getScheduler().ASYNC.run(()-> {
            try {
                MapArt[] arts = ArtMap.instance().getArtDatabase().listMapArt();
                final int total = arts.length;
                int good = 0;
                int repaired = 0;
                int failed = 0;
                int completed = 0;
                long lastUpdate = Long.MIN_VALUE;
                final long UPDATE_INTERVAL = 30 * 1000; //30 seconds
                ArtMap.instance().getLogger().info("Beginning scan of " + total + " artworks");
                for(MapArt art : arts) {
                    boolean result;
                    try {
                        result = this.repairArtwork(art.getMapId(), repair);
                        if(result) {
                            repaired++;
                        } else {
                            good++;
                        }
                    } catch (FileNotFoundException e) {
                        ArtMap.instance().getLogger().log(Level.SEVERE, "Failed to retrieve artwork! :: " + art.getMapId());
                        failed++;
                    }
                    completed++;
                    if(lastUpdate + UPDATE_INTERVAL < System.currentTimeMillis()) {
                        lastUpdate = System.currentTimeMillis();
                        String output = repair ? "Repaired" : "Corrupted";
                        ArtMap.instance().getLogger().info("Scan progress: Completed: " + completed + " Good: " + good + " " + output + ":" + repaired + " Failed: " + failed);
                    }
                    Thread.sleep(100); //sleep 100 
                }
                String output = repair ? "Repaired" : "Corrupted";
                ArtMap.instance().getLogger().info("Scan Complete: Completed: " + completed + " Good: " + good + " " + output + ": " + repaired + " Failed: " + failed);
            } catch (SQLException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failed to retrieve artwork!", e);
            } catch (InterruptedException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Scan interruped!", e);
            }
        });

    }

    /**
     * Check wether the specied artwork can be loaded and if repair is true attempt
     * to fix it.
     * 
     * @param id     The Map ID of the artwork to repair
     * @param repair True - Attempt to repair to artwork.
     * @return
     * @throws FileNotFoundException
     */
    private boolean repairArtwork(int id, boolean repair) throws FileNotFoundException {
        MapArt art = null;
        try {
            art = ArtMap.instance().getArtDatabase().getArtwork(id);
        } catch (SQLException e) {
            return false;
        }
        if(art == null) {
            throw new FileNotFoundException("Artwork with the provided ID does not exist. :: " + id);
        }
        return ArtMap.instance().getArtDatabase().restoreMap(art.getMap(), repair, repair);
    }

}
