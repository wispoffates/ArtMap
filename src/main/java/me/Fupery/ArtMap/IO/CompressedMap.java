package me.Fupery.ArtMap.IO;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.map.MapView;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.ColourMap.f32x32;
import me.Fupery.ArtMap.IO.Database.Map;

public class CompressedMap extends MapId {
    private byte[] compressedMap;

    public CompressedMap(int id, int hash, byte[] compressedMap) {
        super(id, hash);
        this.compressedMap = Arrays.copyOf(compressedMap, compressedMap.length);
    }

    public static CompressedMap compress(MapView mapView) throws IOException {
		return compress(mapView.getId(), ArtMap.instance().getReflection().getMap(mapView));
    }

    public static CompressedMap compress(int mapId, byte[] map) throws IOException {
        byte[] compressed = new f32x32().generateBLOB(map);
        return new CompressedMap(mapId, Arrays.hashCode(map), compressed);
    }

	public static CompressedMap compress(int newId, MapView mapView) throws IOException {
		byte[] compressed = new f32x32().generateBLOB(ArtMap.instance().getReflection().getMap(mapView));
		return new CompressedMap(newId, Arrays.hashCode(ArtMap.instance().getReflection().getMap(mapView)), compressed);
	}

    public byte[] getCompressedMap() {
        return Arrays.copyOf(this.compressedMap, this.compressedMap.length);
    }

    public byte[] decompressMap() {
        return compressedMap == null ? new byte[Map.Size.MAX.value] : new f32x32().readBLOB(compressedMap);
    }
}
