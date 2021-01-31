package me.Fupery.ArtMap.IO.Database;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.Painting.GenericMapRenderer;

public class Map {

    static final byte[] BLANK_MAP = getBlankMap();

    private final int mapId;
    private MapView mapView;

    public Map(int mapId) {
        this.mapId = mapId;
        this.mapView = null;
    }

    public Map(MapView mapView) {
        this.mapId = mapView.getId();
        this.mapView = mapView;
    }

    private static byte[] getBlankMap() {
        byte[] mapOutput = new byte[Size.MAX.value];
        Arrays.fill(mapOutput, ArtMap.instance().getDyePalette().getDefaultColour().getColour());
        return mapOutput;
    }

    public static File getMapDataFolder() {
        String pluginDir = ArtMap.instance().getDataFolder().getParentFile().getAbsolutePath();
        String rootDir = pluginDir.substring(0, pluginDir.lastIndexOf(File.separator));
        // Navigate to this world's data folder);
        return new File(rootDir + File.separator + ArtMap.instance().getConfiguration().WORLD + File.separator + "data");
    }

    public CompressedMap compress() throws IOException {
        return CompressedMap.compress(getMap());
    }

    public byte[] readData() {
        return ArtMap.instance().getReflection().getMap(getMap());
    }

    public void setRenderer(MapRenderer renderer) {
        MapView view = getMap();
        if (view == null) {
            ArtMap.instance().getLogger().warning("MapView is null! :: " + this.getMapId());
            return;
        }
        view.getRenderers().forEach(view::removeRenderer);
        if (renderer != null) {
            view.addRenderer(renderer);
        }
    }

    public Map cloneMap() throws SQLException, IOException, NoSuchFieldException, IllegalAccessException {
        World world = Bukkit.getServer().getWorld(ArtMap.instance().getConfiguration().WORLD);
        if (world == null) {
            ArtMap.instance().getLogger().severe("Tried to create MapView instance for Non-existent world, " + ArtMap.instance().getConfiguration().WORLD);
            return null;
        }

        MapView newMapView = Bukkit.getServer().createMap(world);
        Map newMap = new Map(newMapView);
        byte[] mapData = readData();
        newMap.setMap(mapData);
        ArtMap.instance().getArtDatabase().saveInProgressArt(newMap, mapData);
        return newMap;
    }

    public MapView getMap() {
        if(this.mapView == null) {
            this.mapView = ArtMap.getMap(this.mapId);
        }
        return this.mapView;
    }

    public void clear() throws NoSuchFieldException, IllegalAccessException {
        setMap(BLANK_MAP,false);
    }

    public void setMap(byte[] map) throws NoSuchFieldException, IllegalAccessException {
        setMap(map, true);
    }

    public void setMap(byte[] map, boolean updateRenderer) throws NoSuchFieldException, IllegalAccessException {
        if(this.mapView == null) {
            getMap();
        }
        ArtMap.instance().getReflection().setWorldMap(mapView, map);
        if (updateRenderer) {
            MapRenderer renderer = new GenericMapRenderer(map);
            setRenderer(renderer);
        }
    }

    public boolean exists() {
        return getMap() != null;
    }

    public File getDataFile() {
        return new File(getMapDataFolder(), "map_" + mapId + ".dat");
    }

    public void update(Player player) {
        ArtMap.instance().getScheduler().runSafely(() -> player.sendMap(getMap()));
    }

    public int getMapId() {
        return mapId;
    }

    /*
    private MapView getMapView() {
        //todo We probably don't need sophisticated mapView caching right now
        return (mapView != null) ? mapView :
                (mapView = ArtMap.instance().getScheduler().callSync(() -> Bukkit.getMap(mapId)));
    }*/

    public enum Size {
        MAX(128 * 128), STANDARD(32 * 32);
        public final int value;

        Size(int length) {
            this.value = length;
        }

        public int size() {
            return value;
        }
    }
}
