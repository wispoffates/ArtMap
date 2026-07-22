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
        this.numbers = parseNumbers(plugin.getDescription().getVersion());
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
        return new Version(parseNumbers(bukkit));
    }

    /**
     * Parse the leading numeric dot-segments of a version string, stopping at the
     * first non-numeric segment. Handles suffixes like "1.13.2-R0.1-SNAPSHOT",
     * "7.0.0;02b731f", "7.0.4+f7ff984", bare commit hashes "f7652b23", and
     * Paper's "26.1.2.build.72-stable".
     */
    private static int[] parseNumbers(String version) {
        //chop -R0.1-SNAPSHOT / ;hash / +hash style suffixes off the string
        for (char sep : new char[] {'-', ';', '+'}) {
            int idx = version.indexOf(sep);
            if (idx >= 0) {
                version = version.substring(0, idx);
            }
        }
        String[] strings = version.split("\\.");
        int[] numbers = new int[strings.length];
        int parsed = 0;
        for (String str : strings) {
            try {
                numbers[parsed] = Integer.parseInt(str);
                parsed++;
            } catch (NumberFormatException e) {
                //non-numeric segment like "build" or a commit hash, stop here
                break;
            }
        }
        return (parsed == numbers.length) ? numbers : Arrays.copyOf(numbers, parsed);
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
