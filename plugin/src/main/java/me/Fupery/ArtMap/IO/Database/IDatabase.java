package me.Fupery.ArtMap.IO.Database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Easel.Canvas;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapArt;
import me.Fupery.ArtMap.api.Exception.DuplicateArtworkException;
import me.Fupery.ArtMap.api.Exception.PermissionException;

public interface IDatabase {

    public final Runnable AUTO_SAVE = new Runnable() {
        @Override
        public void run() {
            for (UUID uuid : ArtMap.instance().getArtistHandler().getArtists()) {
                try {
                    ArtMap.instance().getArtistHandler().getCurrentSession(uuid).persistMap(false);
                } catch (SQLException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    ArtMap.instance().getLogger().log(Level.SEVERE,"Error saving artwork!",e);
                }
            }
        }
    };

    /**
     * Retrieve artwork by its title.
     * 
     * @param title The title of the artwork.
     * @return Optionally the artwork.
     * @throws SQLException
     */
    Optional<MapArt> getArtwork(String title) throws SQLException;

    /**
     * Retrieve artwork by its id.
     * 
     * @param id The ID of the artwork.
     * @return The artwork or null if it is not in the database.
     * @throws SQLException
     */
    Optional<MapArt> getArtwork(int id) throws SQLException;

    /**
     * Check if an unsaved artwork exists.
     * 
     * @param id The ID of the artwork.
     * @return True if an unsaved artwork exists.
     * @throws SQLException
     */
    boolean containsUnsavedArtwork(int id) throws SQLException;

    /**
     * Retrieve the compressed map of the artwork
     * 
     * @param id The id of the artwork.
     * @return The compressed map or null if it is not found.
     * @throws SQLException
     */
    Optional<CompressedMap> getArtworkCompressedMap(int id) throws SQLException;

    /**
     * Save a completed piece of art. This method will also update a previous piece
     * of art if the title matches the previous piece.
     * 
     * @param art    The artwork to save.
     * @param title  The title to the artwork to save.
     * @param player The player saving the artwork.
     * @return A copy of the saved artwork or null if the save failed.
     * @throws DuplicateArtworkException
     * @throws PermissionException
     * @throws SQLException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    MapArt saveArtwork(Canvas art, String title, Player player) throws DuplicateArtworkException,
            PermissionException, SQLException, IOException, NoSuchFieldException, IllegalAccessException;

    /**
     * Save artwork but fail if all ready present. This method is used when
     * importing artwork.
     * 
     * @param art  The artwork to save.
     * @param cMap The compressed map of the artwork data.
     * @return True if the artwork was saved. False if it all ready exists.
     * @throws DuplicateArtworkException
     * @throws SQLException
     */
    void saveArtwork(MapArt art, CompressedMap cMap) throws DuplicateArtworkException, SQLException;

    /**
     * Delete an artwork from the database.
     * 
     * @param art The piece of art to delete.
     * @return True if deleted false otherwise.
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    void deleteArtwork(MapArt art) throws SQLException, NoSuchFieldException, IllegalAccessException;

    /**
     * Rename an artwork.
     * 
     * @param art   The artwork to rename.
     * @param title The new title for the artwork.
     * @return True if successful. False otherwise.
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    void renameArtwork(MapArt art, String title)
            throws SQLException, NoSuchFieldException, IllegalAccessException;

    /**
     * Check if the database contains the provided piece of art.
     * 
     * @param artwork     The artwork to check.
     * @param ignoreMapId Whether to ignore the map id when checking if the artwork
     *                    is present.
     * @return True if the artwork is in the datbase. False otherwise.
     * @throws SQLException
     */
    boolean containsArtwork(MapArt artwork, boolean ignoreMapId) throws SQLException;

    /**
     * Check if the database contains an Artwork matching the provided mapId.
     * 
     * @param mapId The mapId to check.
     * @return True if the database contains artwork matching the provided ID. False
     *         if it does not.
     * @throws SQLException
     */
    boolean containsArtwork(int mapId) throws SQLException;

    /**
     * List artwork for the provided artist.
     * 
     * @param artist The UUID of the artisst to lookup.
     * @return A list of the artwork for the artist.
     * @throws SQLException
     */
    List<MapArt> listMapArt(UUID artist) throws SQLException;

    /**
     * List all artwork.
     * 
     * @return A list of all artwrok in the database.
     * @throws SQLException
     */
   List<MapArt> listMapArt() throws SQLException;

    /**
     * List Artists placing the provided player first.
     * 
     * @param player The artist which is first.
     * @return List of artists.
     * @throws SQLException
     */
    List<UUID> listArtists(UUID player) throws SQLException;

    /**
     * List all Artists in the database.
     * 
     * @return List of artists.
     * @throws SQLException
     */
    
    List<UUID> listArtists() throws SQLException;

    List<String> searchArtists(String search);

    List<MapArt> searchArtworks(String search, UUID playerId) throws SQLException;

    /**
     * Prepare to close the database.
     */
    void close();

    //Not assuming that createMap is threadsafe
    /**
     * Create a new Map and initialize it.
     * 
     * @return The newly created Map.
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    Map createMap() throws NoSuchFieldException, IllegalAccessException;

    /**
     * Save an inprogress piece of art to the database.
     * 
     * @param map  The artwork to save.
     * @param data The map data to save.
     * @throws SQLException
     * @throws IOException
     */
    void saveInProgressArt(Map map, byte[] data) throws SQLException, IOException;

    /**
     * Delete an inprogress piece artwork and clear its data.
     * 
     * @param map The artwork to delete.
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    void deleteInProgressArt(Map map) throws SQLException, NoSuchFieldException, IllegalAccessException;

    /**
     * Restore an artwork from the database if it is found to be corrupted. Used in
     * cases were something has happened to damage or delete an artwork's map data
     * file.
     * 
     * @param map The map to restore.
     * @param softRrepair True - attempt to repair the map but do not modify the files on disk. False - Do not attempt to repair just notify.
     * @param hardRrepair True - attempt to repair the map even if it means writing to disk. False - Do not attempt to repair just notify.
     * @return True if a map was found to be corruped.
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    boolean restoreMap(Map map, boolean softRepair, boolean hardRepair);

}