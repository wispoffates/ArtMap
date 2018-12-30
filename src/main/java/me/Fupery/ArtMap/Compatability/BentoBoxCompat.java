package me.Fupery.ArtMap.Compatability;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.Easel.EaselEvent;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

class BentoBoxCompat implements RegionHandler {

    private final boolean loaded;

    public BentoBoxCompat() {
        loaded = Bukkit.getPluginManager().isPluginEnabled("BentoBox");
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        Optional<Island> island = BentoBox.getInstance().getIslands().getIslandAt(location);
        if(island.isPresent()) {
           return island.get().isAllowed(User.getInstance(player), Flags.BREAK_BLOCKS);
        }
        return true;
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        Optional<Island> island = BentoBox.getInstance().getIslands().getIslandAt(entity.getLocation());
        if(island.isPresent()) {
           return island.get().isAllowed(User.getInstance(player), Flags.ARMOR_STAND);
        }
        return true;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
