package me.Fupery.ArtMap.Preview;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

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
        PlayerInventory inventory = player.getInventory();
        if (inventory.getItemInMainHand().getType() != Material.AIR){
            return false;
        }
        inventory.setItemInMainHand(preview);
        return true;
    }

    @Override
    public boolean end(Player player) {
        super.end(player);
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, -2);
        if (player.getItemOnCursor().equals(preview)) {
            player.setItemOnCursor(null);
        } 
        player.getInventory().removeItem(preview);// TODO: 5/08/2016
        return true;
    }

    @Override
    public boolean isEventAllowed(UUID player, Event event) {
        return false;
    }
}
