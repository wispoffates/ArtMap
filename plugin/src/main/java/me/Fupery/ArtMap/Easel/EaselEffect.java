package me.Fupery.ArtMap.Easel;

import org.bukkit.Location;
import org.bukkit.Particle;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Menu.API.SoundCompat;

public enum EaselEffect {
	SPAWN(location -> {/*
	                    * SoundCompat.BLOCK_WOOD_HIT.play(location, 1, 0); Block floorBlock = new
	                    * LocationHelper(location).shiftTowards(BlockFace.DOWN).getBlock();
	                    * location.getWorld().spawnParticle(Particle.BLOCK_DUST, location, 0, 0.10d,
	                    * 0.15d, 0.10d, 0.08d);
	                    */
    }),
    BREAK(location -> {
        SoundCompat.BLOCK_WOOD_BREAK.play(location, 1, -1);
		playEffect(location, Particle.CLOUD);
    }),
    USE_DENIED(location -> {
        SoundCompat.ENTITY_ARMORSTAND_BREAK.play(location);
		playEffect(location, Particle.CRIT);
    }),
    SAVE_ARTWORK(location -> {
		playEffect(location, Particle.VILLAGER_HAPPY);
        SoundCompat.ENTITY_EXPERIENCE_ORB_PICKUP.play(location, 1, 0);
    }),
    MOUNT_CANVAS(location -> {
        SoundCompat.BLOCK_CLOTH_STEP.play(location, 1, 0);
		playEffect(location, Particle.SPELL);
    }),
    START_RIDING(location -> {
        SoundCompat.ENTITY_ITEM_PICKUP.play(location, 1, -3);
    }),
    STOP_RIDING(location -> SoundCompat.BLOCK_LADDER_STEP.play(location, 1, -3));

    private final EffectPlayer effect;

    EaselEffect(EffectPlayer effect) {
        this.effect = effect;
    }

	private static void playEffect(Location loc, Particle effect) {
		// loc.getWorld().spawnParticle(effect, loc, 0, 0.10d, 0.15d, 0.10d, 0.02d);
    }

    public void playEffect(Location location) {
        ArtMap.instance().getScheduler().runSafely(() -> effect.play(location));
    }

    private interface EffectPlayer {
        void play(Location location);
    }
}
