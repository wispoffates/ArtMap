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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

    @BeforeClass
    public static void setup() throws Exception {
        mocks = new MockUtil();
        mocks.mockServer("1.14.4").mockArtMap();
        mockPlugin = mocks.mockDataFolder(new File("target/plugins/Artmap/")).mockLogger()
        .getPluginMock();
    }

    @Test
    public void testDatabaseInit() throws Exception {
        IDatabase db = new Database(mockPlugin);
        Assert.assertNotNull("Database init failure DB is null!", db);
    }

    @Test
    public void testSaveArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mocks.getRandomMockCanvases(1)[0], "test", player);
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player.getName(), savedArt.getArtistName());
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
        Assert.assertNotNull("Failed to retrieve Art!", check);
        Assert.assertEquals("Art title does not match.", "testArt", check.getTitle());
        Assert.assertEquals("Artist ID does not match", player.getUniqueId(), check.getArtist());
        Assert.assertEquals("Artist name does not match", player.getName(), check.getArtistName());
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
        Assert.assertThrows(DuplicateArtworkException.class, () -> {db.saveArtwork(art, mockCompressedMap);});
    }

    @Test
    public void testSaveInprogressArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Map map = new Map(1);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        CompressedMap cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assert.assertNotNull("Database save returned null!", cmap);
        db.deleteInProgressArt(map);// clean the db
        cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assert.assertNull("Delete of in progess artwork failed!", cmap);
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
        Assert.assertNotNull("Database save returned null!", cmap);
        MapArt savedArt = db.saveArtwork(canvas, "inProgressSaved", player);
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player.getName(), savedArt.getArtistName());
        Assert.assertEquals("ID not saved correctly", canvas.getMapId(), savedArt.getMapId());
    }

    @Test
    public void testUpdateInprogressArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);

        Map map = new Map(1);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        db.saveInProgressArt(map, new byte[Map.Size.MAX.value]);
        CompressedMap cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assert.assertNotNull("Database save returned null!", cmap);
        db.deleteInProgressArt(map);// clean the db
        cmap = db.getArtworkCompressedMap(1).orElse(null);
        Assert.assertNull("Delete of in progess artwork failed!", cmap);
    }

    @Test
    public void testSaveArtworkWithDuplicateTitle() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", player);
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player.getName(), savedArt.getArtistName());
        // This save should throw an exception
        Assert.assertThrows("Second save should have thrown a DuplicateArtworkException", DuplicateArtworkException.class,() -> {
            db.saveArtwork(mockCanvas, "test", player);
        });
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player.getName(), savedArt.getArtistName());
        // This save should throw an exception
        Assert.assertThrows("Second save should have thrown a DuplicateArtworkException", DuplicateArtworkException.class,() -> {
            db.saveArtwork(mockCanvasCopy, "test", player);
        });
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", players[0].getName(), savedArt.getArtistName());
        // This save should throw an exception
        Assert.assertThrows("Second save should have thrown a Permission Exception", PermissionException.class,() -> {
            db.saveArtwork(mockCanvasCopy, "test", players[1]);
        });
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", players[0].getName(), savedArt.getArtistName());
        // This save should cause an update
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[0]);
        Assert.assertNotNull("Artwork should have been updated!", savedArt2);
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", players[0].getName(), savedArt.getArtistName());
        //op save should work
        when(players[1].isOp()).thenReturn(true);
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[1]);
        Assert.assertNotNull("Artwork should have been updated!", savedArt2);
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", players[0].getName(), savedArt.getArtistName());
        // Admin update should work
        when(players[1].hasPermission(any(String.class))).thenReturn(true);
        MapArt savedArt2 = db.saveArtwork(mockCanvasCopy, "test", players[1]);
        Assert.assertNotNull("Artwork should have been updated!", savedArt2);
    }

    @Test
    public void testRenameArtwork() throws Exception {
        IDatabase db = new Database(mockPlugin);
        this.clearDatabase(db);
        // mocks
        Canvas mockCanvas = mocks.getRandomMockCanvases(1)[0];
        Player player = mocks.getRandomMockPlayers(1)[0];

        MapArt savedArt = db.saveArtwork(mockCanvas, "test", player);
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player.getName(), savedArt.getArtistName());
        db.renameArtwork(savedArt, "testrename");
        MapArt renamedArt = db.getArtwork("testrename").orElse(null);
        Assert.assertNotNull("Database save returned null!", renamedArt);
        Assert.assertEquals("Art title does not match the rename.", "testrename", renamedArt.getTitle());
        Assert.assertEquals("Art author was changed.", player.getName(), renamedArt.getArtistName());
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        List<UUID> artists = db.listArtists();
        Assert.assertEquals("Should only return 2 artists.",2, artists.size());
        Assert.assertTrue("Player[0] missing from results.", artists.contains(player[0].getUniqueId()));
        Assert.assertTrue("Player[1] missing from results.", artists.contains(player[1].getUniqueId()));
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        List<UUID> artists = db.listArtists(player[0].getUniqueId());
        Assert.assertEquals("Should only return 2 artists.",2, artists.size());
        Assert.assertEquals("Player[0] should be first in the results.", artists.get(0),player[0].getUniqueId());
        Assert.assertEquals("Player[1] should be second in the results.", artists.get(1),player[1].getUniqueId());
        artists = db.listArtists(player[1].getUniqueId());
        Assert.assertEquals("Player[0] should be second in the results.", artists.get(0),player[1].getUniqueId());
        Assert.assertEquals("Player[1] should be firstq in the results.", artists.get(1),player[0].getUniqueId());
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        List<MapArt> artworks = db.listMapArt();
        Assert.assertEquals("Should return 3 artworks.",3, artworks.size());
        Assert.assertTrue("Expected Artwork missing!.", artworks.contains(savedArt));
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        List<MapArt> artworks = db.listMapArt(player[0].getUniqueId());
        Assert.assertEquals("Should return 2 artworks.",2, artworks.size());
        Assert.assertTrue("Expected Artwork missing!.", artworks.contains(savedArt));
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        boolean found = db.containsArtwork(mockCanvas[0].getMapId());
        Assert.assertTrue("Artwork 0 not found.", found);
        found = db.containsArtwork(mockCanvas[1].getMapId());
        Assert.assertTrue("Artwork 1 not found.", found);
        found = db.containsArtwork(mockCanvas[2].getMapId());
        Assert.assertTrue("Artwork 2 not found.", found);
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        boolean found = db.containsArtwork(savedArt,false);
        Assert.assertTrue("Artwork 0 not found.", found);
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
        Assert.assertNotNull("Database save returned null!", savedArt);
        Assert.assertEquals("Artist name not saved correctly", player[0].getName(), savedArt.getArtistName());
        boolean found = db.containsArtwork(test,true);
        Assert.assertTrue("Artwork 0 not found.", found);
    }

    private void clearDatabase(IDatabase db) throws SQLException, NoSuchFieldException, IllegalAccessException {
        List<MapArt> artwork = db.listMapArt();
        for(MapArt art : artwork) {
            db.deleteArtwork(art);
        }
        db.deleteInProgressArt(new Map(1));
    }
}