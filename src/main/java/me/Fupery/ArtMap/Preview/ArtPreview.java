package me.Fupery.ArtMap.Preview;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import me.Fupery.ArtMap.IO.MapArt;

public class ArtPreview extends TimedPreview {

    private ItemStack preview;

    public ArtPreview(MapArt artwork) {
        this.preview = artwork.getMapItem();
    }

    @Override
    public boolean start(Player player) {
        super.start(player);
        if (player.getItemInHand() == null) return false;
        player.setItemInHand(preview);
        return true;
    }

    @Override
    public boolean end(Player player) {
        super.end(player);
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, -2);
        if (player.getItemOnCursor().equals(preview)) player.setItemOnCursor(null);
        player.getInventory().removeItem(preview);// TODO: 5/08/2016
        return true;
    }

    @Override
    public boolean isEventAllowed(UUID player, Event event) {
        return false;
    }
}
