package me.Fupery.ArtMap.easel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.plugin.Plugin;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.mocks.MockUtil;

public class CanvasTest {
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
    public void testArtistParse() {
        List<String> meta = Arrays.asList("test", "by artist");
        String artist = Canvas.parseArtist(meta);
        Assert.assertEquals("The arist should be 'artist'","artist", artist);
    }

    @Test
    public void testArtistParseLonger() {
        List<String> meta = Arrays.asList("test", "test2", "by artist", "test3");
        String artist = Canvas.parseArtist(meta);
        Assert.assertEquals("The arist should be 'artist'","artist", artist);
    }

    @Test
    public void testArtistParseFailure() {
        List<String> meta = Arrays.asList("test", "test2", "artist", "test3");
        String artist = Canvas.parseArtist(meta);
        Assert.assertEquals("The arist should be 'null'",null, artist);
    }

}
