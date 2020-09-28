package me.Fupery.ArtMap.api.Utils;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Version handles the minor changes between different MC versions.
 * Such as dye and sign renames.
 */
public class Version implements Comparable<Version> {
    private final int[] numbers;

    public Version(Plugin plugin) {
        String[] strings = plugin.getDescription().getVersion().split("\\.");
        
        int[] numbers = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            //chop anything like -SNAPSHOT off the version number.
            String str = strings[i];
            if(str.contains("-")) {
                str = str.substring(0, str.indexOf('-'));
            }
            //also wierdness like version: "7.0.0;02b731f"
            if(str.contains(";")) {
                str = str.substring(0, str.indexOf(';'));
            }
            //and this "7.0.4+f7ff984"
            if(str.contains("+")) {
                str = str.substring(0, str.indexOf('+'));
            }
            numbers[i] = Integer.parseInt(str);
        }
        this.numbers = numbers;
    }

    public Version(int... numbers) {
        this.numbers = numbers;
    }

    public static Version getBukkitVersion() {
        String bukkit = Bukkit.getServer().getBukkitVersion();
        return getBukkitVersion(bukkit);
    }

    /**
     * Debug method! Use the no argument method.
     * @param bukkit The Bukkit version string.
     * @return Version Version specific wrapper.
     */
    public static Version getBukkitVersion(String bukkit) {
        String[] ver = bukkit.substring(0, bukkit.indexOf('-')).split("\\.");
        int[] verNumbers = new int[ver.length];
        for (int i = 0; i < ver.length; i++) {
            verNumbers[i] = Integer.parseInt(ver[i]);
        }
        return new Version(verNumbers);
    }

    @Override
    public int compareTo(Version ver) {
        int len = (ver.numbers.length < numbers.length) ? ver.numbers.length : numbers.length;
        for (int i = 0; i < len; i++) {
            int a = i < numbers.length ? numbers[i] : 0;
            int b = i < ver.numbers.length ? ver.numbers[i] : 0;
            if (a != b) {
                return (a > b) ? 1 : -1;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object ver) {
        if(!(ver instanceof Version)) {
            return false;
        }
        Version cVer = (Version) ver;
        return cVer.isEqualTo(this.numbers);
        
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.numbers);
    }

    public boolean isGreaterThan(int... numbers) {
        return compareTo(new Version(numbers)) >= 1;
    }

    public boolean isGreaterOrEqualTo(int... numbers) {
        return compareTo(new Version(numbers)) >= 0;
    }

    public boolean isEqualTo(int... numbers) {
        return compareTo(new Version(numbers)) == 0;
    }

    public boolean isLessOrEqualTo(int... numbers) {
        return compareTo(new Version(numbers)) <= 0;
    }

    public boolean isLessThan(int... numbers) {
        return compareTo(new Version(numbers)) <= -1;
    }

    @Override
    public String toString() {
        if (numbers.length == 0) return "0";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < numbers.length; i++) {
            sb.append(numbers[i]);
            if (i < numbers.length - 1) {
                sb.append('.');
            }
        }
        return sb.toString();
    }
}
