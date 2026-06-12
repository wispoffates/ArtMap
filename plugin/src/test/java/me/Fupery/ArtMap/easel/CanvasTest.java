package me.Fupery.ArtMap.easel;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.mocks.MockUtil;

public class CanvasTest {

    @BeforeAll
    public static void setup() throws Exception {
        MockUtil mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
        // parseArtist matches lore against the RECIPE_ARTWORK_ARTIST lang key,
        // so the lang file must be loaded for the "by <artist>" prefix to resolve.
        ArtMap mockArtmap = mocks.getArtmapMock();
        Lang.load(mockArtmap, new Configuration(mockArtmap));
    }

    @Test
    public void testArtistParse() {
        List<String> meta = Arrays.asList("test", "by artist");
        String artist = Canvas.parseArtist(meta).get();
        Assertions.assertEquals("artist", artist, "The arist should be 'artist'");
    }

    @Test
    public void testArtistParseLonger() {
        List<String> meta = Arrays.asList("test", "test2", "by artist", "test3");
        String artist = Canvas.parseArtist(meta).get();
        Assertions.assertEquals("artist", artist, "The arist should be 'artist'");
    }

    @Test
    public void testArtistParseFailure() {
        List<String> meta = Arrays.asList("test", "test2", "artist", "test3");
        String artist = Canvas.parseArtist(meta).orElse(null);
        Assertions.assertEquals(null, artist, "The arist should be 'null'");
    }

}
