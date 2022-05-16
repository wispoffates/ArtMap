package me.Fupery.ArtMap.Command;

import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Preview.ArtPreview;

class CommandPreview extends AsyncCommand {

    CommandPreview() {
        super(null, "/art preview <title>", false);
    }

    private static boolean previewArtwork(final Player player, final MapArt art) {
        if (ArtMap.instance().getConfiguration().FORCE_GUI) {
            player.sendMessage("Please use the Paint Brush to access previews.");
            return false;
        }

        ArtMap.instance().getPreviewManager().endPreview(player);

        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
            return false;
        }

        ArtMap.instance().getPreviewManager().startPreview(player, new ArtPreview(art));
        
        return true;
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {

        Player player = (Player) sender;

        Optional<MapArt> art;
        try {
            art = ArtMap.instance().getArtDatabase().getArtwork(args[1]);
        } catch (SQLException e) {
            sender.sendMessage("Error loading preview!");
            ArtMap.instance().getLogger().log(Level.SEVERE,"Error loading preview!",e);
            return;
        }

        if (!art.isPresent()) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (!previewArtwork(player, art.get())) {
            msg.message = Lang.EMPTY_HAND_PREVIEW.get();
            return;
        }
        msg.message = String.format(Lang.PREVIEWING.get(), args[1]);
    }
}
