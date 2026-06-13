package me.Fupery.ArtMap.Painting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Compatibility.impl.Palette_1_18;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Easel;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.PixelTableManager;
import me.Fupery.ArtMap.IO.Database.Database;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.IO.Protocol.ProtocolHandler;
import me.Fupery.ArtMap.IO.Protocol.In.PacketReceiver;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket;
import me.Fupery.ArtMap.IO.Protocol.In.Packet.ArtistPacket.PacketInteract.InteractType;
import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.DyeType;
import me.Fupery.ArtMap.api.Colour.Palette;
import me.Fupery.ArtMap.api.Config.Configuration;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.mocks.MockUtil;

/**
 * Integration test that simulates a painting client. Packets are injected at
 * the exact seam where the network layer hands off to the plugin
 * (ArtistHandler.handlePacket), so everything downstream runs for real:
 * cursor trig, brushes, renderer, persistence, compression and SQLite.
 */
@TestMethodOrder(OrderAnnotation.class)
public class PaintingIntegrationTest {

    private static final int CANVAS_MAP_ID = 500;
    private static final int FILL_MAP_ID = 501;

    private static MockUtil mocks;
    private static ArtistHandler handler;
    private static Database db;
    private static Palette palette;
    private static PixelTableManager pixelTable;
    private static Player painter;

    /** What each painted pixel should decompress to, keyed by (x,y). */
    private static final HashMap<Long, Byte> expectedPixels = new HashMap<>();

    @BeforeAll
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.21.4-R0.1-SNAPSHOT").mockArtMap();
        mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger();
        ArtMap plugin = mocks.getArtmapMock();

        Lang.load(plugin, new Configuration(plugin));
        // onEnable normally does this; the packet handler consults ArtMaterial
        // to tell paint brushes apart from dyes. Recipes themselves aren't needed.
        when(plugin.getRecipeLoader()).thenReturn(mock(me.Fupery.ArtMap.Recipe.RecipeLoader.class));
        me.Fupery.ArtMap.Recipe.ArtMaterial.setupRecipes();
        // Use the real modern palette so every production colour is exercised.
        when(plugin.getDyePalette()).thenReturn(new Palette_1_18());
        palette = plugin.getDyePalette();

        db = new Database(plugin);
        when(plugin.getArtDatabase()).thenReturn(db);

        // The packet receiver is the network layer we are simulating around.
        ProtocolHandler protocol = mock(ProtocolHandler.class);
        when(protocol.getPacketReceiver()).thenReturn(mock(PacketReceiver.class));
        when(plugin.getProtocolManager()).thenReturn(protocol);

        handler = new ArtistHandler();
        when(plugin.getArtistHandler()).thenReturn(handler);

        pixelTable = plugin.getPixelTable();
        painter = mocks.getRandomMockPlayers(1)[0];

        // The Art.db under target/ survives between runs; deleting the artwork
        // also wipes its map data, so do it before any painting happens.
        Optional<MapArt> leftover = db.getArtwork("PaintingIT");
        if (leftover.isPresent()) {
            db.deleteArtwork(leftover.get());
        }
    }

    /** The yaw a client looks at to put the cursor in pixel column x. */
    private static float yawFor(int x) {
        float[] bounds = pixelTable.getYawBounds();
        return (bounds[x] + bounds[x + 1]) / 2f;
    }

    /** The pitch a client looks at to put the cursor in pixel row y of column x. */
    private static float pitchFor(int x, int y) {
        float[] bounds = (float[]) pixelTable.getPitchBounds()[x];
        return (bounds[y] + bounds[y + 1]) / 2f;
    }

    /** Look at pixel (x,y) then click, like a painting client does. */
    private static void lookAndClick(int x, int y, long cooldownMillis) throws InterruptedException {
        handler.handlePacket(painter, new ArtistPacket.PacketLook(yawFor(x), pitchFor(x, y)));
        Thread.sleep(cooldownMillis); // respect the brush stroke cooldown
        handler.handlePacket(painter, new ArtistPacket.PacketInteract(InteractType.ATTACK));
    }

    private static Easel mockEasel() {
        Easel easel = mock(Easel.class);
        when(easel.seatUser(any(Player.class))).thenReturn(true);
        return easel;
    }

    private static byte pixelAt(byte[] mapData, int x, int y) {
        int resolution = pixelTable.getResolutionFactor();
        return mapData[(x * resolution) + (y * resolution * 128)];
    }

    @Test
    @Order(1)
    public void paintEveryColourThroughPackets() throws Exception {
        Map map = new Map(CANVAS_MAP_ID);
        handler.addPlayer(painter, mockEasel(), map, 0);
        assertNotNull(handler.getCurrentSession(painter), "Session should be registered after addPlayer");

        ArtDye[] dyes = palette.getDyes(DyeType.DYE);
        assertTrue(dyes.length > 0, "Palette should expose dyes");
        int axis = 128 / pixelTable.getResolutionFactor();

        for (int i = 0; i < dyes.length; i++) {
            ArtDye dye = dyes[i];
            // Spread the strokes across the canvas, one pixel per colour.
            int x = (i % (axis - 2)) + 1;
            int y = ((i / (axis - 2)) * 3) + 1;

            painter.getInventory().setItemInMainHand(new ItemStack(dye.getMaterial()));
            lookAndClick(x, y, 160);

            // Some dyes share a material; record what the palette will resolve.
            ArtDye resolved = palette.getDye(new ItemStack(dye.getMaterial()));
            expectedPixels.put((long) x << 32 | y, resolved.getDyeColour((byte) 0));
        }

        // Dismounting persists the in-progress painting, like a player standing up.
        handler.removePlayer(painter);

        Optional<CompressedMap> stored = db.getArtworkCompressedMap(CANVAS_MAP_ID);
        assertTrue(stored.isPresent(), "In-progress artwork should be persisted on dismount");
        byte[] mapData = stored.get().decompressMap();

        for (java.util.Map.Entry<Long, Byte> expected : expectedPixels.entrySet()) {
            int x = (int) (expected.getKey() >> 32);
            int y = expected.getKey().intValue();
            assertEquals(expected.getValue().byteValue(), pixelAt(mapData, x, y),
                    "Pixel (" + x + "," + y + ") should hold the painted colour");
        }
    }

    @Test
    @Order(2)
    public void fillBucketFloodsTheCanvas() throws Exception {
        Map map = new Map(FILL_MAP_ID);
        handler.addPlayer(painter, mockEasel(), map, 0);

        ArtDye red = palette.getDye(new ItemStack(Material.RED_DYE));
        assertNotNull(red, "RED dye should resolve from the palette");
        painter.getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
        painter.getInventory().setItemInOffHand(new ItemStack(Material.RED_DYE));

        int axis = 128 / pixelTable.getResolutionFactor();
        lookAndClick(axis / 2, axis / 2, 400); // fill brush has a 350ms cooldown

        handler.removePlayer(painter);
        painter.getInventory().setItemInOffHand(new ItemStack(Material.AIR));

        byte[] mapData = db.getArtworkCompressedMap(FILL_MAP_ID)
                .orElseThrow(() -> new AssertionError("Fill painting should be persisted"))
                .decompressMap();
        byte expected = red.getDyeColour((byte) 0);
        for (int x = 0; x < axis; x++) {
            for (int y = 0; y < axis; y++) {
                assertEquals(expected, pixelAt(mapData, x, y),
                        "Flood fill should colour pixel (" + x + "," + y + ")");
            }
        }
    }

    /**
     * Regression test: end() used to run teleport/kit-removal and persistMap in
     * one try block, so any dismount error (here: a teleport NPE from a null
     * location) silently discarded the player's unsaved strokes.
     */
    @Test
    @Order(4)
    public void dismountErrorMustNotLoseThePainting() throws Exception {
        Player brokenPlayer = mock(Player.class);
        when(brokenPlayer.getUniqueId()).thenReturn(java.util.UUID.randomUUID());
        when(brokenPlayer.getName()).thenReturn("BrokenTeleportPlayer");
        org.bukkit.inventory.PlayerInventory inv = mock(org.bukkit.inventory.PlayerInventory.class);
        // Created before stubbing: the ItemStack constructor calls into the
        // mocked server, which would corrupt an in-progress when() otherwise.
        ItemStack air = new ItemStack(Material.AIR);
        ItemStack[] mainHand = { air };
        when(inv.getItemInMainHand()).thenAnswer(i -> mainHand[0]);
        when(inv.getItemInOffHand()).thenReturn(air);
        when(brokenPlayer.getInventory()).thenReturn(inv);
        // getLocation() stays unstubbed -> null -> teleport NPEs inside end()

        int mapId = 502;
        handler.addPlayer(brokenPlayer, mockEasel(), new Map(mapId), 0);

        // One stroke at the cursor's starting position (canvas centre).
        mainHand[0] = new ItemStack(Material.RED_DYE);
        Thread.sleep(200);
        handler.handlePacket(brokenPlayer, new ArtistPacket.PacketInteract(InteractType.ATTACK));

        handler.removePlayer(brokenPlayer);

        byte[] mapData = db.getArtworkCompressedMap(mapId)
                .orElseThrow(() -> new AssertionError("Painting should be persisted despite the dismount error"))
                .decompressMap();
        byte red = palette.getDye(new ItemStack(Material.RED_DYE)).getDyeColour((byte) 0);
        boolean foundStroke = false;
        for (byte b : mapData) {
            if (b == red) {
                foundStroke = true;
                break;
            }
        }
        assertTrue(foundStroke, "The red stroke must survive a dismount error");
    }

    @Test
    @Order(3)
    public void savingPersistsThePaintedArtwork() throws Exception {
        // Save the multi-colour painting from the first test as a titled artwork.
        Map map = new Map(CANVAS_MAP_ID);
        Canvas canvas = new Canvas(map, painter.getName());

        MapArt saved = db.saveArtwork(canvas, "PaintingIT", painter);
        assertNotNull(saved, "saveArtwork should return the new artwork");
        assertEquals(painter.getName(), saved.getArtistName());

        assertTrue(db.getArtwork("PaintingIT").isPresent(), "Artwork should be retrievable by title");

        byte[] mapData = db.getArtworkCompressedMap(CANVAS_MAP_ID)
                .orElseThrow(() -> new AssertionError("Saved artwork should keep its map data"))
                .decompressMap();
        for (java.util.Map.Entry<Long, Byte> expected : expectedPixels.entrySet()) {
            int x = (int) (expected.getKey() >> 32);
            int y = expected.getKey().intValue();
            assertEquals(expected.getValue().byteValue(), pixelAt(mapData, x, y),
                    "Saved pixel (" + x + "," + y + ") should survive the save");
        }

        // cleanup so reruns start fresh
        db.deleteArtwork(saved);
    }
}
