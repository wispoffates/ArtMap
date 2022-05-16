package me.Fupery.ArtMap.Command;

import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;

class CommandDelete extends AsyncCommand {

    CommandDelete() {
		super(null, "/art delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
		if (ArtMap.instance().getConfiguration().FORCE_GUI) {
			sender.sendMessage("Please use the Paint Brush to access the artwork for delete.");
			return;
		}
        try {
            Optional<MapArt> art = ArtMap.instance().getArtDatabase().getArtwork(args[1]);

            if (!art.isPresent()) {
                msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
                return;
            }
            if (sender instanceof Player
                    && !(art.get().getArtistPlayer().getUniqueId().equals(((Player) sender).getUniqueId())
                    || sender.hasPermission("artmap.admin"))) {
                msg.message = Lang.NO_PERM.get();
                return;
            }
            ArtMap.instance().getArtDatabase().deleteArtwork(art.get());
            msg.message = String.format(Lang.DELETED.get(), args[1]);
        } catch(Exception e) {
            sender.sendMessage("Failure deleting art! Check logs for details.");
            ArtMap.instance().getLogger().log(Level.SEVERE, "Failure deleting art!", e);
        } 
    }
}
