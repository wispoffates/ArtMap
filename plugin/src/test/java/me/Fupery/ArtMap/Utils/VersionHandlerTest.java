package me.Fupery.ArtMap.Utils;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.api.Utils.VersionHandler;
import me.Fupery.ArtMap.api.Utils.VersionHandler.BukkitVersion;
import me.Fupery.ArtMap.mocks.MockUtil;

public class VersionHandlerTest {

    private static MockUtil mocks;

    @BeforeAll
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
    }

    @Test
    public void test_v1_13() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_13, mocks.getPluginMock()).getVersion();
        assertTrue(version.isEqualTo(BukkitVersion.v1_13), "Should equal to 1.13 :: " + version.toString());
        assertTrue(version.isLessOrEqualTo(BukkitVersion.v1_13), "Should less than or equal to 1.13");
        assertTrue(version.isLessThan(BukkitVersion.v1_14), "Should be less than to 1.14");

        //should fail through to bedrock
        assertNotNull(version.getSign(), "Sign should not be null!");
        assertTrue(version.getSign().name().equals("BEDROCK"), "1.13 should be just sign. :: " + version.getSign().name());
    }

    @Test
    public void test_v1_14() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_14, mocks.getPluginMock()).getVersion();
        assertTrue(version.isGreaterThan(BukkitVersion.v1_13), "Should be greater than 1.13 :: " + version.toString());
        assertTrue(version.isGreaterOrEqualTo(BukkitVersion.v1_14), "Should be greater than or equal to to 1.14 :: " + version.toString());
        assertTrue(version.isEqualTo(BukkitVersion.v1_14), "Should equal to 1.14 :: " + version.toString());
        assertTrue(version.isLessOrEqualTo(BukkitVersion.v1_14), "Should less than or equal to 1.14");
        assertTrue(version.isLessThan(BukkitVersion.v1_15), "Should be less than to 1.15");

        assertNotNull(version.getSign(), "Sign should not be null!");
        assertTrue(version.getSign().name().equals("OAK_SIGN"), "1.14 should be oak_sign. :: " + version.getSign().name());
    }

    @Test
    public void test_v1_15() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_15, mocks.getPluginMock()).getVersion();
        assertTrue(version.isGreaterThan(BukkitVersion.v1_14), "Should be greater than 1.14 :: " + version.toString());
        assertTrue(version.isGreaterOrEqualTo(BukkitVersion.v1_15), "Should be greater than or equal to to 1.15 :: " + version.toString());
        assertTrue(version.isEqualTo(BukkitVersion.v1_15), "Should equal to 1.15 :: " + version.toString());
        assertTrue(version.isLessOrEqualTo(BukkitVersion.v1_15), "Should less than or equal to 1.15");
        assertTrue(version.isLessThan(BukkitVersion.v1_16), "Should be less than to 1.16");
    }

    @Test
    public void test_v1_16() {
        BukkitVersion version = new VersionHandler(BukkitVersion.v1_16, mocks.getPluginMock()).getVersion();
        assertTrue(version.isGreaterThan(BukkitVersion.v1_15), "Should be greater than 1.15 :: " + version.toString());
        assertTrue(version.isGreaterOrEqualTo(BukkitVersion.v1_16), "Should be greater than or equal to to 1.16 :: " + version.toString());
        assertTrue(version.isEqualTo(BukkitVersion.v1_16), "Should equal to 1.16 :: " + version.toString());
        assertTrue(version.isLessOrEqualTo(BukkitVersion.v1_16), "Should less than or equal to 1.16");
    }

    @Test
    public void test_latest() {
        BukkitVersion version = VersionHandler.getLatest();
        assertTrue(version.isGreaterThan(BukkitVersion.v1_15), "Should be greater than 1.15 :: " + version.toString());
        assertTrue(version.isGreaterOrEqualTo(BukkitVersion.v1_16), "Should be greater than or equal to 1.16 :: " + version.toString());
        assertTrue(version.isEqualTo(BukkitVersion.v1_20_2), "Should equal to 1.20.2 :: " + version.toString());
        assertTrue(version.isLessOrEqualTo(BukkitVersion.v1_20_2), "Should less than or equal to 1.18");
    }

}
