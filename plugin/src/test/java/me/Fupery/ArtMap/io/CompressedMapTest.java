package me.Fupery.ArtMap.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.mocks.MockUtil;

public class CompressedMapTest {

    @BeforeAll
    public static void setup() throws Exception {
        // Map's static init needs ArtMap.instance() for the default dye colour.
        MockUtil mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
    }

    // 128x128 data with one colour per 4x4 cell (f32x32 lossless input)
    private byte[] blockyMapData() {
        byte[] data = new byte[Map.Size.MAX.value];
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                data[x + y * 128] = (byte) (((x / 4) + (y / 4) * 32) % 127);
            }
        }
        return data;
    }

    @Test
    public void compressRoundTripsFullResolutionData() throws IOException {
        byte[] data = blockyMapData();
        CompressedMap map = CompressedMap.compress(42, data);
        assertEquals(42, map.getId());
        assertEquals(Arrays.hashCode(data), map.getHash().intValue(), "Hash should be of the uncompressed data");
        assertArrayEquals(data, map.decompressMap(), "Decompressed data should match the original");
    }

    @Test
    public void compressRejectsInvalidSize() {
        assertThrows(IOException.class, () -> CompressedMap.compress(1, new byte[100]));
    }

    @Test
    public void compressedBlobIsSmallerThanSource() throws IOException {
        byte[] data = blockyMapData();
        CompressedMap map = CompressedMap.compress(1, data);
        assertTrue(map.getCompressedMap().length < data.length,
                "BLOB should be smaller than the raw 16KB map");
    }
}
