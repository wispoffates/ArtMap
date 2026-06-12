package me.Fupery.ArtMap.easel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.api.Exception.ArtMapException;

/**
 * Exercises Canvas.getCanvas() against real ItemStacks built through
 * MockBukkit's ItemFactory rather than hand-stubbed metas.
 */
public class CanvasItemTest {

    private static final String UNFINISHED_TAG = ChatColor.AQUA.toString() + ChatColor.ITALIC + "Unfinished Artwork";

    private static ServerMock server;
    private static WorldMock world;

    @BeforeAll
    public static void setup() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("test");
    }

    @AfterAll
    public static void teardown() {
        MockBukkit.unmock();
    }

    private ItemStack unfinishedArtwork(MapView view) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);
        meta.setLore(Arrays.asList(UNFINISHED_TAG, "by TestArtist"));
        item.setItemMeta(meta);
        return item;
    }

    @Test
    public void getCanvasOnUnfinishedArtwork() throws Exception {
        MapView view = server.createMap(world);
        Optional<Canvas> canvas = Canvas.getCanvas(unfinishedArtwork(view));
        assertTrue(canvas.isPresent(), "Unfinished artwork should produce a canvas");
        assertEquals(view.getId(), canvas.get().getMapId(), "Canvas should carry the map view's id");
    }

    @Test
    public void getCanvasWithoutMapViewIsEmpty() throws Exception {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setLore(Arrays.asList(UNFINISHED_TAG));
        item.setItemMeta(meta);
        Optional<Canvas> canvas = Canvas.getCanvas(item);
        assertFalse(canvas.isPresent(), "A filled map without a map view has no canvas");
    }

    @Test
    public void getCanvasOnNonMapThrows() {
        assertThrows(ArtMapException.class, () -> Canvas.getCanvas(new ItemStack(Material.DIRT)));
    }

    @Test
    public void getCanvasOnNullThrows() {
        assertThrows(ArtMapException.class, () -> Canvas.getCanvas(null));
    }
}
