package me.Fupery.ArtMap.Preview;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.Menu.API.SoundCompat;

public class ArtPreview extends TimedPreview {

    private ItemStack preview;

    public ArtPreview(MapArt artwork) {
        this.preview = artwork.getMapItem();
    }

    @Override
    public boolean start(Player player) {
        super.start(player);
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInOffHand().getType() != Material.AIR){
            Lang.EMPTY_HAND_PREVIEW.send(player);
            return false;
        }
        inventory.setItemInOffHand(preview);
        return true;
    }

    @Override
    public boolean end(Player player) {
        super.end(player);
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, -2);
        if (player.getItemOnCursor().equals(preview)) {
            player.setItemOnCursor(null);
        } 
        if (player.getInventory().getItemInOffHand().equals(preview)) {
            player.getInventory().setItemInOffHand(null);
        }
        player.getInventory().removeItem(preview);
        return true;
    }

    @Override
    public boolean isEventAllowed(UUID player, Event event) {
        return false;
    }
}
