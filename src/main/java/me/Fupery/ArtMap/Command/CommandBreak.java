package me.Fupery.ArtMap.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.Utils.ItemUtils;

class CommandBreak extends AsyncCommand {

    CommandBreak() {
		super("artmap.artist", "/art break", false);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        final Player player = (Player) sender;

        if (!ArtMap.getArtistHandler().containsPlayer(player)) {
            Lang.NOT_RIDING_EASEL.send(player);
            return;
        }

        ArtMap.getScheduler().SYNC.run(() -> {
            Easel easel = null;
            easel = ArtMap.getArtistHandler().getEasel(player);

            if (easel == null) {
                Lang.NOT_RIDING_EASEL.send(player);
                return;
            }
            ArtMap.getArtistHandler().removePlayer(player);
            ArtMap.getArtDatabase().recycleMap(new Map(ItemUtils.getMapID(easel.getItem())));
            easel.removeItem();
			easel.breakEasel();
        });
    }
}
