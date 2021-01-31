package me.Fupery.ArtMap.Command;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Painting.ArtSession;
import me.Fupery.ArtMap.api.Config.Lang;

class CommandClear extends AsyncCommand {

    CommandClear() {
        super("artmap.artist", "/art clear", false);
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
                ArtSession session = ArtMap.instance().getArtistHandler().getCurrentSession(player);
                session.clearMap();
                session.persistMap(true);
            } catch (NoSuchFieldException | IllegalAccessException | SQLException | IOException e) {
                sender.sendMessage("Failure breaking the easel! Check the server logs.");
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure breaking easel!", e);
            }
        });
    }
}
