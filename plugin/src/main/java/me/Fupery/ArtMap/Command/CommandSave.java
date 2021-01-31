package me.Fupery.ArtMap.Command;

import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.Easel.EaselEffect;
import me.Fupery.ArtMap.Exception.DuplicateArtworkException;
import me.Fupery.ArtMap.Exception.PermissionException;
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
        if (ArtMap.instance().getConfiguration().FORCE_GUI) {
            Lang.PAINTBRUSH_FORCED.send(sender);
            return;
        }
        if(args.length<2) {
            Lang.SAVE_USAGE.send(sender);
            return;
        }

        final String title = args[1];

        final Player player = (Player) sender;

        if (!this.filter.check(title)) {
            msg.message = Lang.BAD_TITLE.get();
            return;
        }

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
                Canvas canvas = Canvas.getCanvas(easel.getItem());
                MapArt art1 = ArtMap.instance().getArtDatabase().saveArtwork(canvas, title, player);
                easel.playEffect(EaselEffect.SAVE_ARTWORK);
                ArtMap.instance().getArtistHandler().removePlayer(player);
                easel.setItem(new ItemStack(Material.AIR));
                ItemUtils.giveItem(player, art1.getMapItem());
                player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
            } catch (DuplicateArtworkException | PermissionException e) {
                player.sendMessage(e.getMessage());
            } catch (Exception e) {
                msg.message = String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title);
                ArtMap.instance().getLogger().log(Level.SEVERE, "Database error!", e);
            }
        });
    }
}
