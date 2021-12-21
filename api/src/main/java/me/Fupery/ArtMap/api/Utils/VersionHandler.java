package me.Fupery.ArtMap.api.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;


public class VersionHandler {
    private final BukkitVersion version;
    private static JavaPlugin plugin;

    public VersionHandler(JavaPlugin plugin) {
        this.version = checkVersion();
        this.plugin = plugin;
    }

    /**
     * Unit tests
     * Do not use this.
     */
    public VersionHandler(BukkitVersion bv, JavaPlugin plugin) {
        version = bv;
        this.plugin = plugin;
    }

    public static BukkitVersion checkVersion() {
        Version version = Version.getBukkitVersion();
        if (version.isLessThan(1, 14)) return BukkitVersion.v1_13;
        else if (version.isLessThan(1, 15)) return BukkitVersion.v1_14;
        else if (version.isLessThan(1, 16)) return BukkitVersion.v1_15;
        else if (version.isLessThan(1, 17)) return BukkitVersion.v1_16;
        else if (version.isLessThan(1, 18)) return BukkitVersion.v1_17;
		else
			return BukkitVersion.v1_18;
    }

    public static BukkitVersion getLatest() {
        BukkitVersion[] handlers = BukkitVersion.values();
        return handlers[handlers.length - 1];
    }

    public BukkitVersion getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return version.toString();
    }

    public enum BukkitVersion {
		UNKNOWN, v1_13, v1_14, v1_15, v1_16, v1_17, v1_18;

        public boolean isGreaterThan(BukkitVersion version) {
            return ordinal() > version.ordinal();
        }

        public boolean isGreaterOrEqualTo(BukkitVersion version) {
            return ordinal() >= version.ordinal();
        }

        public boolean isEqualTo(BukkitVersion version) {
            return ordinal() == version.ordinal();
        }

        public boolean isLessOrEqualTo(BukkitVersion version) {
            return ordinal() <= version.ordinal();
        }

        public boolean isLessThan(BukkitVersion version) {
            return ordinal() < version.ordinal();
        }

        public float getEulerValue(Object packet, String methodName) throws NoSuchMethodException,
                InvocationTargetException, IllegalAccessException {
            Method method = packet.getClass().getMethod(methodName, float.class);
            method.setAccessible(true);
            return (float) method.invoke(packet, (float) 0);
        }

        public float getYaw(Object packet) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {
            return getEulerValue(packet, "a");
        }

        public float getPitch(Object packet) throws NoSuchMethodException,
                IllegalAccessException, InvocationTargetException {
            return getEulerValue(packet, "b");
        }

        public double getSeatXOffset() {
            return 1.219;
        }

        public double getSeatYOffset() {
            return -2.24979;
        }

        public Material getWallSign() {
            String matName = "OAK_WALL_SIGN";
            if( this.isEqualTo(BukkitVersion.v1_13)) {
                matName = "WALL_SIGN";
            } 
            Material mat = Material.getMaterial(matName, false);
            if(mat == null) {
                plugin.getLogger().warning("Failed to get wall sign material! :: " + matName);
                mat = Material.BEDROCK;
            }
            return mat;
        }

        public Material getSign() {
            String matName = "OAK_SIGN";
            if( this.isEqualTo(BukkitVersion.v1_13)) {
                matName = "SIGN";
            } 
            Material mat = Material.getMaterial(matName, false);
            if(mat == null) {
                plugin.getLogger().warning("Failed to get sign material! :: " + matName);
                mat = Material.BEDROCK;
            }
            return mat;
        }

    }

}
