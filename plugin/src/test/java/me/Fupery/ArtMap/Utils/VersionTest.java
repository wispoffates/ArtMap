package me.Fupery.ArtMap.Utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.api.Utils.Version;
import me.Fupery.ArtMap.mocks.MockUtil;

public class VersionTest {

    private static MockUtil mocks;

    @BeforeAll
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
    }

    @Test
    public void test_v1_13_lessThan() {
        Version version = Version.getBukkitVersion("1.13.2-R0.1-SNAPSHOT");
        assertTrue(version.isLessThan(1,14), "Version should be less then 1.13 but was " + version.toString());
    }

    @Test
    public void test_v1_13_lessThanEqualto() {
        Version version = Version.getBukkitVersion("1.13.2-R0.1-SNAPSHOT");
        assertTrue(version.isLessOrEqualTo(1,13), "Version should be equal to 1.13 but was " + version.toString());
    }
    @Test
    public void test_v1_14_greaterThan() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        assertTrue(version.isGreaterThan(1,13), "Version should be greater then 1.13 but was " + version.toString());
    }

    @Test
    public void test_v1_14_greaterThanEqualTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        assertTrue(version.isGreaterOrEqualTo(1,14), "Version should be equal then 1.14 but was " + version.toString());
    }

    @Test
    public void test_v1_14_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        assertTrue(version.isEqualTo(1,14), "Version should be equal then 1.14 but was " + version.toString());
    }

    @Test
    public void test_v1_14_2_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        assertTrue(version.isEqualTo(1,14,2), "Version should be equal then 1.14 but was " + version.toString());
    }

    @Test
    public void test_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version2 = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version3 = Version.getBukkitVersion("1.15.2-R0.1-SNAPSHOT");
        assertTrue(version.equals(version2), "Version should be equal." + version.toString());
        assertFalse(version.equals(version3), "Version should be equal." + version.toString());
    }

    @Test
    public void test_hashcode() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version2 = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version3 = Version.getBukkitVersion("1.15.2-R0.1-SNAPSHOT");
        assertTrue(version.hashCode() == version2.hashCode(), "Version should be equal." + version.toString());
        assertFalse(version.hashCode() == version3.hashCode(), "Version should be equal." + version.toString());
    }

    // Minecraft switched to year-based version numbers (e.g. 26.1) — make sure
    // the parser and comparisons survive the new scheme.

    @Test
    public void test_v26_parse() {
        Version version = Version.getBukkitVersion("26.1-R0.1-SNAPSHOT");
        assertTrue(version.isEqualTo(26,1), "26.1 should parse from the bukkit string but was " + version.toString());
    }

    @Test
    public void test_v26_parseWithPatch() {
        Version version = Version.getBukkitVersion("26.1.2-R0.1-SNAPSHOT");
        assertTrue(version.isEqualTo(26,1,2), "26.1.2 should parse but was " + version.toString());
        assertTrue(version.isGreaterThan(26,1,1), "26.1.2 should be greater than 26.1.1");
        assertTrue(version.isLessThan(26,2), "26.1.2 should be less than 26.2");
    }

    @Test
    public void test_v26_parseWithoutSuffix() {
        // Version strings are not guaranteed to carry the -R0.1-SNAPSHOT suffix.
        Version version = Version.getBukkitVersion("26.1");
        assertTrue(version.isEqualTo(26,1), "Suffix-less 26.1 should parse but was " + version.toString());
    }

    @Test
    public void test_v26_sortsAfterLegacyVersions() {
        Version legacy = Version.getBukkitVersion("1.21.4-R0.1-SNAPSHOT");
        Version yearBased = Version.getBukkitVersion("26.1-R0.1-SNAPSHOT");
        assertTrue(yearBased.isGreaterThan(1,21,4), "26.1 should be greater than 1.21.4");
        assertTrue(legacy.isLessThan(26,1), "1.21.4 should be less than 26.1");
        assertTrue(yearBased.compareTo(legacy) > 0, "26.1 should compare after 1.21.4");
    }

    @Test
    public void test_pluginVersion() {
        Version version = new Version(mocks.getArtmapMock());
        assertTrue(version.isGreaterOrEqualTo(3,5,3), "Plugin version should be equal.");
    }


    @Test
    public void test_loadRegionHandler() {
        assertTrue(loadRegionHandler(new Version(4,5), new Version(4), new Version(5)), "4.5 should be in between.");
        assertTrue(loadRegionHandler(new Version(4,5), new Version(4), new Version(9999)), "4.5 should be in between.");
        assertTrue(loadRegionHandler(new Version(4), new Version(4), new Version(5)), "4 is inclusive.");
        assertFalse(loadRegionHandler(new Version(5), new Version(4), new Version(5)), "5 is exclusive and should fail.");
        assertFalse(loadRegionHandler(new Version(5,1), new Version(4), new Version(5)), "5.1 is outside and should fail.");
        assertFalse(loadRegionHandler(new Version(3,9), new Version(4), new Version(5)), "3.9 is outside and should fail.");
        assertFalse(loadRegionHandler(new Version(2), new Version(4), new Version(5)), "2 is outside and should fail.");
        assertFalse(loadRegionHandler(new Version(7), new Version(4), new Version(5)), "7 is outside and should fail.");
    }

    //Dumbed down logic of compatibilityManager version test to make sure my logic is correct.
    private boolean loadRegionHandler(Version test, Version lower, Version upper) {
        try {
            return lower.compareTo(test) == 0 && upper.compareTo(test) > 0;
        } catch (Throwable exception) {
            return false;
        }
    }
}
