package me.Fupery.ArtMap.Utils;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import me.Fupery.ArtMap.api.Utils.Version;
import me.Fupery.ArtMap.mocks.MockUtil;

public class VersionTest {
 
    private static MockUtil mocks;
    private static Plugin mockPlugin;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4-R0.1-MOCK").mockArtMap();
        mockPlugin = mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger()
        .getPluginMock();
    }

    @Test
    public void test_v1_13_lessThan() {
        Version version = Version.getBukkitVersion("1.13.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be less then 1.13 but was " + version.toString(), version.isLessThan(1,14));
    }

    @Test
    public void test_v1_13_lessThanEqualto() {
        Version version = Version.getBukkitVersion("1.13.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal to 1.13 but was " + version.toString(), version.isLessOrEqualTo(1,13));
    }
    @Test
    public void test_v1_14_greaterThan() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be greater then 1.13 but was " + version.toString(), version.isGreaterThan(1,13));
    }
    
    @Test
    public void test_v1_14_greaterThanEqualTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal then 1.14 but was " + version.toString(), version.isGreaterOrEqualTo(1,14));
    }

    @Test
    public void test_v1_14_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal then 1.14 but was " + version.toString(), version.isEqualTo(1,14));
    }

    @Test
    public void test_v1_14_2_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal then 1.14 but was " + version.toString(), version.isEqualTo(1,14,2));
    }

    @Test
    public void test_equalTo() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version2 = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version3 = Version.getBukkitVersion("1.15.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal." + version.toString(), version.equals(version2));
        Assert.assertFalse("Version should be equal." + version.toString(), version.equals(version3));
    }

    @Test
    public void test_hashcode() {
        Version version = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version2 = Version.getBukkitVersion("1.14.2-R0.1-SNAPSHOT");
        Version version3 = Version.getBukkitVersion("1.15.2-R0.1-SNAPSHOT");
        Assert.assertTrue("Version should be equal." + version.toString(), version.hashCode() == version2.hashCode());
        Assert.assertFalse("Version should be equal." + version.toString(), version.hashCode() == version3.hashCode());
    }

    @Test
    public void test_pluginVersion() {
        Version version = new Version(mocks.getArtmapMock());
        Assert.assertTrue("Plugin version should be equal.", version.isGreaterOrEqualTo(3,5,3));
    }


    @Test
    public void test_loadRegionHandler() {
        Assert.assertTrue("4.5 should be in between.",loadRegionHandler(new Version(4,5), new Version(4), new Version(5)));
        Assert.assertTrue("4.5 should be in between.",loadRegionHandler(new Version(4,5), new Version(4), new Version(9999)));
        Assert.assertTrue("4 is inclusive.",loadRegionHandler(new Version(4), new Version(4), new Version(5)));
        Assert.assertFalse("5 is exclusive and should fail.",loadRegionHandler(new Version(5), new Version(4), new Version(5)));
        Assert.assertFalse("5.1 is outside and should fail.",loadRegionHandler(new Version(5,1), new Version(4), new Version(5)));
        Assert.assertFalse("3.9 is outside and should fail.",loadRegionHandler(new Version(3,9), new Version(4), new Version(5)));
        Assert.assertFalse("2 is outside and should fail.",loadRegionHandler(new Version(2), new Version(4), new Version(5)));
        Assert.assertFalse("7 is outside and should fail.",loadRegionHandler(new Version(7), new Version(4), new Version(5)));
    }

    //Dumbed down logic of compatibilityManager version test to make sure my logic is correct.
    private boolean loadRegionHandler(Version test, Version lower, Version upper) {
        System.out.println("lower: " + lower.compareTo(test));
        System.out.println("upper: " + upper.compareTo(test));
        try {
            if(lower.compareTo(test) == 0 && upper.compareTo(test) > 0) {
                return true;
            } else {
                return false;
            }
        } catch (Throwable exception) {
            return false;
        }
    }
}