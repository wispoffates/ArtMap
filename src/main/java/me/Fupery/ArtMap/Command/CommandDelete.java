package me.Fupery.ArtMap.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;

class CommandDelete extends AsyncCommand {

    CommandDelete() {
		super(null, "/art delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
		if (ArtMap.getConfiguration().FORCE_GUI) {
			sender.sendMessage("Please use the Paint Brush to access the artwork for delete.");
			return;
		}

        MapArt art = ArtMap.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (sender instanceof Player
                && !(art.getArtistPlayer().getUniqueId().equals(((Player) sender).getUniqueId())
                || sender.hasPermission("artmap.admin"))) {
            msg.message = Lang.NO_PERM.get();
            return;
        }
        if (ArtMap.getArtDatabase().deleteArtwork(art)) {
            msg.message = String.format(Lang.DELETED.get(), args[1]);
        } else {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
        }
    }
}
