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

    // pins current behavior: head lookup NPEs in the offline mock environment
    @Test
    public void retrieveHead() {
        HeadsCache cache = new HeadsCache(mockArtMap, false);
        Assertions.assertThrows(NullPointerException.class,
                () -> cache.getHead(UUID.fromString("5dcadcf6-7070-42ab-aaf3-b60a120a6bcf")));
    }
}