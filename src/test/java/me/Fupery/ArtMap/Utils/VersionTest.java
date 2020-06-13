package me.Fupery.ArtMap.Utils;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import me.Fupery.ArtMap.mocks.MockUtil;

public class VersionTest {
 
    private static MockUtil mocks;
    private static Plugin mockPlugin;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.15.2-R0.1-MOCK").mockArtMap();
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
}