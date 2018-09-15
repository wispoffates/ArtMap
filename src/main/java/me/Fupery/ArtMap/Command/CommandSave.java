package me.Fupery.ArtMap.Command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Config.Lang;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.TitleFilter;
import me.Fupery.ArtMap.Utils.ItemUtils;

class CommandSave extends AsyncCommand {

    private TitleFilter filter;

    CommandSave() {
		super("artmap.artist", "/art save <title>", false);
        this.filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
		if (ArtMap.getConfiguration().FORCE_GUI) {
			sender.sendMessage("Please use the Paint Brush to save.");
			return;
		}

        final String title = args[1];

        final Player player = (Player) sender;

		if (!this.filter.check(title)) {
            msg.message = Lang.BAD_TITLE.get();
            return;
        }

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
            easel.playEffect(EaselEffect.SAVE_ARTWORK);
            ArtMap.getArtistHandler().removePlayer(player);

			Canvas canvas = Canvas.getCanvas(easel.getItem());
			MapArt art1 = ArtMap.getArtDatabase().saveArtwork(canvas, title, player);
			if (art1 != null) {
				easel.setItem(new ItemStack(Material.AIR));
				ItemUtils.giveItem(player, art1.getMapItem());
				player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
			} else {
				player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title));
			}
        });
    }
}
