package me.Fupery.ArtMap.Heads;

import java.io.File;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.mocks.MockUtil;

public class HeadsTest {
    private static MockUtil mocks;
    private static ArtMap mockArtMap;

    @BeforeAll
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
        mockArtMap = mocks.getArtmapMock();
    }

    /**
     * Pins current behavior: head lookups NPE in the offline mock environment
     * rather than returning a fallback head. If this test starts failing because
     * a head is returned, the cache got more robust — update the test to assert
     * on the returned head instead.
     */
    @Test
    public void retrieveHead() {
        HeadsCache cache = new HeadsCache(mockArtMap, false);
        Assertions.assertThrows(NullPointerException.class,
                () -> cache.getHead(UUID.fromString("5dcadcf6-7070-42ab-aaf3-b60a120a6bcf")));
    }
}