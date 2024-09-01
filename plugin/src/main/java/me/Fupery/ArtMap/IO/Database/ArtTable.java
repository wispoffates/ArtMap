package me.Fupery.ArtMap.IO.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.MapArt;

final class ArtTable extends SQLiteTable {

    ArtTable(SQLiteDatabase database) {
        super(database, "artworks", "CREATE TABLE IF NOT EXISTS artworks (" +
                "title   varchar(32)       NOT NULL UNIQUE," +
                "id      INT               NOT NULL UNIQUE," +
                "artist  varchar(32)       NOT NULL," +
                "date    varchar(32)       NOT NULL," +
                "PRIMARY KEY (title)" +
                ");");
    }

    Optional<MapArt> getArtwork(String title) throws SQLException {
        return new QueuedQuery<Optional<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }

            @Override
			protected Optional<MapArt> read(ResultSet set) throws SQLException {
                return (set.next()) ? Optional.of(readArtwork(set)) : Optional.empty();
            }
        }.execute("SELECT * FROM " + table + " WHERE title=?;");
    }


    Optional<MapArt> getArtwork(int mapData) throws SQLException {
        return new QueuedQuery<Optional<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapData);
            }

            @Override
			protected Optional<MapArt> read(ResultSet set) throws SQLException {
                return (set.next()) ? Optional.of(readArtwork(set)) : Optional.empty();
            }
        }.execute("SELECT * FROM " + table + " WHERE id=?;");
    }

    List<MapArt> searchArtwork(String title) throws SQLException {
        return new QueuedQuery<List<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, '%' + title + '%');
            }

            @Override
			protected List<MapArt> read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks;
            }
        }.execute("SELECT * FROM " + table + " WHERE title LIKE ? ORDER BY artist;");
    }

    List<MapArt> searchArtwork(String title, UUID playerId) throws SQLException {
        return new QueuedQuery<List<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, '%' + title + '%');
                statement.setString(2, playerId.toString());
            }

            @Override
			protected List<MapArt> read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks;
            }
        }.execute("SELECT * FROM " + table + " WHERE title LIKE ? AND artist = ? ORDER BY title;");
    }

    MapArt readArtwork(ResultSet set) throws SQLException {
        String title = set.getString("title");
        int id = set.getInt("id");
        UUID artist = UUID.fromString(set.getString("artist"));
        String date = set.getString("date");
        String name = "Unknown";
        if(ArtMap.instance().getHeadsCache().isHeadCached(artist)) {
            name = ArtMap.instance().getHeadsCache().getPlayerName(artist);
        } else {
            name = Bukkit.getOfflinePlayer(artist).getName();
        }
        return new MapArt(id, title, artist,name,date);
    }


    boolean containsArtwork(MapArt art, boolean ignoreMapID) throws SQLException {
        return new QueuedQuery<Boolean>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
            }


            @Override
			protected Boolean read(ResultSet set) throws SQLException {
				return set.isBeforeFirst();
            }
        }.execute("SELECT title FROM " + table + " WHERE title=?;")
                && (ignoreMapID || containsMapID(art.getMapId()));
    }


    boolean containsMapID(int mapID) throws SQLException {
        return new QueuedQuery<Boolean>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapID);
            }

            @Override
			protected Boolean read(ResultSet set) throws SQLException {
				return set.isBeforeFirst();
            }
        }.execute("SELECT id FROM " + table + " WHERE id=?;");
    }


    void deleteArtwork(String title) throws SQLException {
        new QueuedStatement() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, title);
            }
        }.execute("DELETE FROM " + table + " WHERE title=?;");
    }

	void renameArtwork(MapArt art, String nTitle) throws SQLException {
		new QueuedStatement() {

			@Override
			protected void prepare(PreparedStatement statement) throws SQLException {
				statement.setString(1, nTitle);
				statement.setInt(2, art.getMapId());
			}
		}.execute("UPDATE " + table + " SET title=? WHERE id=?;");
	}

    void deleteArtwork(int mapId) throws SQLException {
        new QueuedStatement() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }
        }.execute("DELETE FROM " + table + " WHERE id=?;");
    }


    List<MapArt> listMapArt(UUID artist) throws SQLException {
        return new QueuedQuery<List<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, artist.toString());
            }

            @Override
			protected List<MapArt> read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks;
            }
        }.execute("SELECT * FROM " + table + " WHERE artist = ? ORDER BY title;");
    }

    List<MapArt> listMapArt() throws SQLException {
        return new QueuedQuery<List<MapArt>>() {

            @Override
			protected void prepare(PreparedStatement statement) {
                //nothing to set
            }

            @Override
			protected List<MapArt> read(ResultSet results) throws SQLException {
                ArrayList<MapArt> artworks = new ArrayList<>();
                while (results.next()) {
                    artworks.add(readArtwork(results));
                }
                return artworks;
            }
        }.execute("SELECT * FROM " + table + " ORDER BY id;");
    }

    List<UUID> listArtists(UUID player) throws SQLException {
        return new QueuedQuery<List<UUID>>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, player.toString());
            }

            @Override
			protected List<UUID> read(ResultSet results) throws SQLException {
                ArrayList<UUID> artists = new ArrayList<>();
				if (player != null) {
					artists.add(0, player);
				}
                while (results.next()) {
                    artists.add(UUID.fromString(results.getString("artist")));
                }
                return artists;
            }
        }.execute("SELECT DISTINCT artist FROM " + table + " WHERE artist != ? ORDER BY artist;");
    }

	List<UUID> listArtists() throws SQLException {
		return new QueuedQuery<List<UUID>>() {

            @Override
			protected void prepare(PreparedStatement statement) {
                //no values to set
            }

            @Override
			protected List<UUID> read(ResultSet results) throws SQLException {
                ArrayList<UUID> artists = new ArrayList<>();
                while (results.next()) {
                    artists.add(UUID.fromString(results.getString("artist")));
                }
                return artists;
            }
        }.execute("SELECT DISTINCT artist FROM " + table + " ORDER BY artist;");
	}

    void updateMapID(MapArt art) throws SQLException {
        new QueuedStatement() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, art.getMapId());
                statement.setString(2, art.getTitle());
            }
        }.execute("UPDATE " + table + " SET id=? WHERE title=?;");
    }

	void addArtwork(MapArt art) throws SQLException {
		new QueuedStatement() {
            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setString(1, art.getTitle());
                statement.setInt(2, art.getMapId());
                statement.setString(3, art.getArtist().toString());
                statement.setString(4, art.getDate());
            }
        }.execute("INSERT INTO " + table + " (title, id, artist, date) VALUES(?,?,?,?);");
    }
}
