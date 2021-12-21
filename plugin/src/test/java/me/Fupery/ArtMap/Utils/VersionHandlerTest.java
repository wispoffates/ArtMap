package me.Fupery.ArtMap.Utils;

import java.io.File;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import me.Fupery.ArtMap.api.Utils.VersionHandler;
import me.Fupery.ArtMap.api.Utils.VersionHandler.BukkitVersion;
import me.Fupery.ArtMap.mocks.MockUtil;

public class VersionHandlerTest {
    
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
    public void test_v1_13() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_13, mocks.getPluginMock()).getVersion();
        Assert.assertTrue("Should equal to 1.13 :: " + version.toString(), version.isEqualTo(BukkitVersion.v1_13));
        Assert.assertTrue("Should less than or equal to 1.13", version.isLessOrEqualTo(BukkitVersion.v1_13));
        Assert.assertTrue("Should be less than to 1.14", version.isLessThan(BukkitVersion.v1_14));

        //should fail through to bedrock
        Assert.assertNotNull("Sign should not be null!", version.getSign());
        Assert.assertTrue("1.13 should be just sign. :: " + version.getSign().name(), version.getSign().name().equals("BEDROCK"));
    }

    @Test
    public void test_v1_14() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_14, mocks.getPluginMock()).getVersion();
        Assert.assertTrue("Should be greater than 1.13 :: " + version.toString(), version.isGreaterThan(BukkitVersion.v1_13));
        Assert.assertTrue("Should be greater than or equal to to 1.14 :: " + version.toString(), version.isGreaterOrEqualTo(BukkitVersion.v1_14));
        Assert.assertTrue("Should equal to 1.14 :: " + version.toString(), version.isEqualTo(BukkitVersion.v1_14));
        Assert.assertTrue("Should less than or equal to 1.14", version.isLessOrEqualTo(BukkitVersion.v1_14));
        Assert.assertTrue("Should be less than to 1.15", version.isLessThan(BukkitVersion.v1_15));

        Assert.assertNotNull("Sign should not be null!", version.getSign());
        Assert.assertTrue("1.14 should be oak_sign. :: " + version.getSign().name(), version.getSign().name().equals("OAK_SIGN"));
    }

    @Test
    public void test_v1_15() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_15, mocks.getPluginMock()).getVersion();
        Assert.assertTrue("Should be greater than 1.14 :: " + version.toString(), version.isGreaterThan(BukkitVersion.v1_14));
        Assert.assertTrue("Should be greater than or equal to to 1.15 :: " + version.toString(), version.isGreaterOrEqualTo(BukkitVersion.v1_15));
        Assert.assertTrue("Should equal to 1.15 :: " + version.toString(), version.isEqualTo(BukkitVersion.v1_15));
        Assert.assertTrue("Should less than or equal to 1.15", version.isLessOrEqualTo(BukkitVersion.v1_15));
        Assert.assertTrue("Should be less than to 1.16", version.isLessThan(BukkitVersion.v1_16));
    }

    @Test
    public void test_v1_16() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_16, mocks.getPluginMock()).getVersion();
        Assert.assertTrue("Should be greater than 1.15 :: " + version.toString(), version.isGreaterThan(BukkitVersion.v1_15));
        Assert.assertTrue("Should be greater than or equal to to 1.16 :: " + version.toString(), version.isGreaterOrEqualTo(BukkitVersion.v1_16));
        Assert.assertTrue("Should equal to 1.16 :: " + version.toString(), version.isEqualTo(BukkitVersion.v1_16));
        Assert.assertTrue("Should less than or equal to 1.16", version.isLessOrEqualTo(BukkitVersion.v1_16));
        //Assert.assertTrue("Should be less than to 1.15", version.isLessThan(BukkitVersion.v1_15));
    }

    @Test
    public void test_latest() {
        BukkitVersion version = VersionHandler.getLatest();
        Assert.assertTrue("Should be greater than 1.15 :: " + version.toString(), version.isGreaterThan(BukkitVersion.v1_15));
        Assert.assertTrue("Should be greater than or equal to to 1.16 :: " + version.toString(), version.isGreaterOrEqualTo(BukkitVersion.v1_16));
        Assert.assertTrue("Should equal to 1.18 :: " + version.toString(), version.isEqualTo(BukkitVersion.v1_18));
        Assert.assertTrue("Should less than or equal to 1.18", version.isLessOrEqualTo(BukkitVersion.v1_18));
        //Assert.assertTrue("Should be less than to 1.15", version.isLessThan(BukkitVersion.v1_15));
    }

}