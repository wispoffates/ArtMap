package me.Fupery.ArtMap.io.Database;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.Easel.Canvas.CanvasCopy;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.IO.Database.Database;
import me.Fupery.ArtMap.IO.Database.IDatabase;
import me.Fupery.ArtMap.IO.Database.Map;
import me.Fupery.ArtMap.api.Exception.DuplicateArtworkException;
import me.Fupery.ArtMap.api.Exception.PermissionException;
import me.Fupery.ArtMap.mocks.MockUtil;

public class DatabaseTest {

    private static MockUtil mocks;
    private static Plugin mockPlugin;

    private static final File DB_FILE = new File("target/plugins/Artmap/Art.db");

    @BeforeAll
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mockPlugin = mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger()
        .getPluginMock();
    }

    @BeforeEach
    public void resetSharedMocks() {
        mocks.resetMockPlayerPermissions();
        // start from an empty db: Art.db persists between methods and runs, and
        // leftover rows (esp. map id 1) made testSaveArtworkImportDuplicate flaky
        if (DB_FILE.exists() && !DB_FILE.delete()) {
            throw new IllegalStateException("Could not delete stale test database: " + DB_FILE);
        }
    }

    @Test
    public void testDatabaseInit() throws Exception {
        IDatabase db = new Database(mockPlugin);
        Assertions.assertNotNull(db, "Database init failure DB is null!");
    }

    @Test
    public void testSaveArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mocks.getRandomMockCanvases(1)[0], "test", player);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player.getName(), savedArt.getArtistName(), "Artist name not saved correctly");
    }

    @Test
    public void testSaveArtworkImport() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Player player = mocks.getRandomMockPlayers(1)[0];
        // mock CompressedMap
        CompressedMap mockCompressedMap = CompressedMap.compress(1, new byte[Map.Size.MAX.value]);
        MapArt art = new MapArt(1, "testArt", player.getUniqueId(), player.getName(), new Date());

        db.saveArtwork(art, mockCompressedMap);
        MapArt check = db.getArtwork(1).orElse(null);
        Assertions.assertNotNull(check, "Failed to retrieve Art!");
        Assertions.assertEquals("testArt", check.getTitle(), "Art title does not match.");
        Assertions.assertEquals(player.getUniqueId(), check.getArtist(), "Artist ID does not match");
        Assertions.assertEquals(player.getName(), check.getArtistName(), "Artist name does not match");
    }

    @Test
    public void testSaveArtworkImportDuplicate() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Player player = mocks.getRandomMockPlayers(1)[0];
        // mock CompressedMap
        CompressedMap mockCompressedMap = CompressedMap.compress(1, new byte[Map.Size.MAX.value]);
        MapArt art = new MapArt(1, "testArt", player.getUniqueId(), player.getName(), new Date());

        db.saveArtwork(art, mockCompressedMap);
         // should throw the exception
        Assertions.assertThrows(DuplicateArtworkException.class, () -> {db.saveArtwork(art, mockCompressedMap);});
    }

    @Test
    public void testSaveInprogressArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Map map = new Map(1);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        CompressedMap cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assertions.assertNotNull(cmap, "Database save returned null!");
        db.deleteInProgressArt(map);// clean the db
        cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assertions.assertNull(cmap, "Delete of in progess artwork failed!");
    }

    @Test
    public void testCompleteInprogressArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Player player = mocks.getRandomMockPlayers(1)[0];
        Canvas canvas = mocks.getRandomMockCanvases(1)[0];

        Map map = new Map(canvas.getMapId());
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        CompressedMap cmap = db.getArtworkCompressedMap(canvas.getMapId()).orElse(null);
        Assertions.assertNotNull(cmap, "Database save returned null!");
        MapArt savedArt = db.saveArtwork(canvas, "inProgressSaved", player);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player.getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        Assertions.assertEquals(canvas.getMapId(), savedArt.getMapId(), "ID not saved correctly");
    }

    @Test
    public void testUpdateInprogressArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Map map = new Map(1);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        CompressedMap cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assertions.assertNotNull(cmap, "Database save returned null!");
        db.deleteInProgressArt(map);// clean the db
        cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assertions.assertNull(cmap, "Delete of in progess artwork failed!");
    }

    @Test
    public void testSaveArtworkWithDuplicateTitle() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", player);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player.getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        // This save should throw an exception
        Assertions.assertThrows(DuplicateArtworkException.class, () -> {
            db.saveArtwork(mockCanvas, "test", player);
        }, "Second save should have thrown a DuplicateArtworkException");
    }

    @Test
    public void testSaveArtworkWithDuplicateTitleWithCanvasCopyButDifferentID() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvases = mocks.getRandomMockCanvases(2);
        Canvas mockCanvasCopy = mocks.mockCanvasCopy(mockCanvases[1]);
        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mockCanvases[0], "test", player);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player.getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        // This save should throw an exception
        Assertions.assertThrows(DuplicateArtworkException.class, () -> {
            db.saveArtwork(mockCanvasCopy, "test", player);
        }, "Second save should have thrown a DuplicateArtworkException");
    }

    @Test
    public void testSaveArtworkWrongPlayer() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        CanvasCopy mockCanvasCopy = mocks.mockCanvasCopy(mockCanvas);
        Player[] players = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", players[0]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(players[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        // This save should throw an exception
        Assertions.assertThrows(PermissionException.class, () -> {
            db.saveArtwork(mockCanvasCopy, "test", players[1]);
        }, "Second save should have thrown a Permission Exception");
    }

    @Test
    public void testUpdateArtworkSamePlayer() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        CanvasCopy mockCanvasCopy = mocks.mockCanvasCopy(mockCanvas);
        Player[] players = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", players[0]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(players[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        // This save should cause an update
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[0]);
        Assertions.assertNotNull(savedArt2, "Artwork should have been updated!");
    }

    @Test
    public void testUpdateArtworkOpPlayer() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        CanvasCopy mockCanvasCopy = mocks.mockCanvasCopy(mockCanvas);
        Player[] players = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", players[0]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(players[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        //op save should work
        when(players[1].isOp()).thenReturn(true);
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[1]);
        Assertions.assertNotNull(savedArt2, "Artwork should have been updated!");
    }

    @Test
    public void testUpdateArtworkAdminPlayer() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        CanvasCopy mockCanvasCopy = mocks.mockCanvasCopy(mockCanvas);
        Player[] players = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", players[0]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(players[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        // Admin update should work
        when(players[1].hasPermission(any(String.class))).thenReturn(true);
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[1]);
        Assertions.assertNotNull(savedArt2, "Artwork should have been updated!");
    }

    @Test
    public void testRenameArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", player);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player.getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        db.renameArtwork(savedArt, "testrename");
        MapArt renamedArt = db.getArtwork("testrename").orElse(null);
        Assertions.assertNotNull(renamedArt, "Database save returned null!");
        Assertions.assertEquals("testrename", renamedArt.getTitle(), "Art title does not match the rename.");
        Assertions.assertEquals(player.getName(), renamedArt.getArtistName(), "Art author was changed.");
    }

    @Test
    public void testListArtists() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        List<UUID> artists = db.listArtists();
        Assertions.assertEquals(2, artists.size(), "Should only return 2 artists.");
        Assertions.assertTrue(artists.contains(player[0].getUniqueId()), "Player[0] missing from results.");
        Assertions.assertTrue(artists.contains(player[1].getUniqueId()), "Player[1] missing from results.");
    }

    @Test
    public void testListArtistsSkip() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        List<UUID> artists = db.listArtists(player[0].getUniqueId());
        Assertions.assertEquals(2, artists.size(), "Should only return 2 artists.");
        Assertions.assertEquals(artists.get(0), player[0].getUniqueId(), "Player[0] should be first in the results.");
        Assertions.assertEquals(artists.get(1), player[1].getUniqueId(), "Player[1] should be second in the results.");
        artists = db.listArtists(player[1].getUniqueId());
        Assertions.assertEquals(artists.get(0), player[1].getUniqueId(), "Player[0] should be second in the results.");
        Assertions.assertEquals(artists.get(1), player[0].getUniqueId(), "Player[1] should be firstq in the results.");
    }

    @Test
    public void testListArt() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        List<MapArt> artworks = db.listMapArt();
        Assertions.assertEquals(3, artworks.size(), "Should return 3 artworks.");
        Assertions.assertTrue(artworks.contains(savedArt), "Expected Artwork missing!.");
    }

    @Test
    public void testListArtForArtist() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        List<MapArt> artworks = db.listMapArt(player[0].getUniqueId());
        Assertions.assertEquals(2, artworks.size(), "Should return 2 artworks.");
        Assertions.assertTrue(artworks.contains(savedArt), "Expected Artwork missing!.");
    }

    @Test
    public void testContainsArtByID() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        boolean found = db.containsArtwork(mockCanvas[0].getMapId());
        Assertions.assertTrue(found, "Artwork 0 not found.");
        found = db.containsArtwork(mockCanvas[1].getMapId());
        Assertions.assertTrue(found, "Artwork 1 not found.");
        found = db.containsArtwork(mockCanvas[2].getMapId());
        Assertions.assertTrue(found, "Artwork 2 not found.");
    }

    @Test
    public void testContainsArtByMapArt() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        boolean found = db.containsArtwork(savedArt,false);
        Assertions.assertTrue(found, "Artwork 0 not found.");
    }

    @Test
    public void testContainsArtByMapArtDifferentID() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas[] mockCanvas = mocks.getRandomMockCanvases(3);
        Player[] player = mocks.getRandomMockPlayers(2);

        MapArt savedArt = db.saveArtwork(mockCanvas[0], "test", player[0]);
        MapArt test = new MapArt(32, savedArt.getTitle(), savedArt.getArtistPlayer().getUniqueId(), savedArt.getArtistName(),savedArt.getDate());
        db.saveArtwork(mockCanvas[1], "test2", player[0]);
        db.saveArtwork(mockCanvas[2], "testPlayer2_1", player[1]);
        Assertions.assertNotNull(savedArt, "Database save returned null!");
        Assertions.assertEquals(player[0].getName(), savedArt.getArtistName(), "Artist name not saved correctly");
        boolean found = db.containsArtwork(test,true);
        Assertions.assertTrue(found, "Artwork 0 not found.");
    }

    private void clearDatabase(IDatabase db) throws SQLException, NoSuchFieldException, IllegalAccessException {
        List<MapArt> artwork = db.listMapArt();
        for(MapArt art : artwork) {
            db.deleteArtwork(art);
        }
        db.deleteInProgressArt(new Map(1));
    }
}