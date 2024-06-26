package me.Fupery.ArtMap.Heads;

import java.io.File;
import java.util.UUID;

import org.apache.commons.lang.ObjectUtils.Null;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Exception.HeadFetchException;
import me.Fupery.ArtMap.mocks.MockUtil;

public class HeadsTest {
    private static MockUtil mocks;
    private static ArtMap mockPlugin;
    private static ArtMap mockArtMap;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mockPlugin = mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger().getPluginMock();
        mockArtMap = mocks.getArtmapMock();
    }

    @Test(expected = NullPointerException.class)
    public void retrieveHead() throws HeadFetchException {
        HeadsCache cache = new HeadsCache(mockArtMap, false);
        ItemStack head = cache.getHead(UUID.fromString("5dcadcf6-7070-42ab-aaf3-b60a120a6bcf"));
        Assert.assertNotNull(head);
    }
}