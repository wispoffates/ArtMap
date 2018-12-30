package me.Fupery.ArtMap.Compatability;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.EnginePermBuild;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ps.PS;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.Fupery.ArtMap.Easel.EaselEvent;

class FactionsCompat implements RegionHandler {
    private boolean loaded = false;

    FactionsCompat() {
        Factions.get();
        EnginePermBuild.get();
        MassiveCore.get();
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        PS loc = PS.valueOf(location);
        Faction hostFaction = BoardColl.get().getTerritoryAccessAt(loc).getHostFaction();
        return hostFaction == null || EnginePermBuild.canPlayerBuildAt(player, loc, true);
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        PS loc = PS.valueOf(entity.getLocation());
        Faction hostFaction = BoardColl.get().getTerritoryAccessAt(loc).getHostFaction();
        // I don't know about this null
        return hostFaction == null || EnginePermBuild.useEntity(player, entity, false, null);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
