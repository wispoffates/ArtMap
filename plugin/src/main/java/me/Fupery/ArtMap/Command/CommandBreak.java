package me.Fupery.ArtMap.Command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Exception.ArtMapException;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Utils.ItemUtils;

class CommandBreak extends AsyncCommand {

    CommandBreak() {
        super("artmap.artist", "/art break", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        final Player player = (Player) sender;

        if (!ArtMap.instance().getArtistHandler().containsPlayer(player)) {
            Lang.NOT_RIDING_EASEL.send(player);
            return;
        }

        ArtMap.instance().getScheduler().SYNC.run(() -> {
            Easel easel = null;
            easel = ArtMap.instance().getArtistHandler().getEasel(player);

            if (easel == null) {
                Lang.NOT_RIDING_EASEL.send(player);
                return;
            }
            try {
                ArtMap.instance().getArtistHandler().removePlayer(player);
                ArtMap.instance().getArtDatabase().deleteInProgressArt(new Map(ItemUtils.getMapID(easel.getItem())));
                easel.removeItem();
                easel.breakEasel();
            } catch (SQLException | IOException | NoSuchFieldException | IllegalAccessException | ArtMapException e) {
                sender.sendMessage("Failure breaking the easel! Check the server logs.");
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure breaking easel!", e);
            }
        });
    }
}
