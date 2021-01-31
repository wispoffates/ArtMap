package me.Fupery.ArtMap.IO.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.MapId;

final class MapTable extends SQLiteTable {
    public MapTable(SQLiteDatabase database) {
        super(database, "maps", "CREATE TABLE IF NOT EXISTS maps (" +
                "id   INT   NOT NULL UNIQUE," +
                "hash INT   NOT NULL," +
                "map  BLOB  NOT NULL," +
                "PRIMARY KEY (id)" +
                ");");
    }

    void addMap(CompressedMap map) throws SQLException {
        new QueuedStatement() {
            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getId());
                statement.setInt(2, map.getHash());
                statement.setBytes(3, map.getCompressedMap());
            }
        }.execute("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
    }

    void updateMapId(int oldMapId, int newMapId) throws SQLException {
        new QueuedStatement() {
            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, newMapId);
                statement.setInt(2, oldMapId);
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE id=?;");
    }

    Void deleteMap(int mapId) throws SQLException {
        return new QueuedStatement() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE id=?;");
    }

    boolean containsMap(int mapId) throws SQLException {
        return new QueuedQuery<Boolean>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
            protected Boolean read(ResultSet set) throws SQLException {
				return set.isBeforeFirst();
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }

    void updateMap(CompressedMap map) throws SQLException {
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getHash());
                statement.setBytes(2, map.getCompressedMap());
                statement.setInt(3, map.getId());
            }
        }.execute("UPDATE " + TABLE + " SET hash=?, map=? WHERE id=?;");
    }

    CompressedMap getMap(int mapId) throws SQLException {
        return new QueuedQuery<CompressedMap>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
			protected CompressedMap read(ResultSet set) throws SQLException {
                if (!set.next()) return null;
                int id = set.getInt("id");
                int hash = set.getInt("hash");
                byte[] map = set.getBytes("map");
                return new CompressedMap(id, hash, map);
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE id=?;");
    }

    Integer getHash(int mapId) throws SQLException {
        return new QueuedQuery<Integer>() {

            @Override
			protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
			protected Integer read(ResultSet set) throws SQLException {
                return (set.next()) ? set.getInt("hash") : null;
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }


    List<MapId> getMapIds() throws SQLException {
        return new QueuedQuery<List<MapId>>() {

            @Override
			protected void prepare(PreparedStatement statement) {
                //not needed
            }

            @Override
			protected List<MapId> read(ResultSet set) throws SQLException {
                List<MapId> mapHashes = new ArrayList<>(set.getFetchSize());
                while (set.next()) {
                    mapHashes.add(new MapId(set.getInt("id"), set.getInt("hash")));
                }
                return mapHashes;
            }
        }.execute("SELECT id, hash FROM " + TABLE + ";");
    }

    /**
     * @param maps A list of maps to add to the database
     * @return A list of maps that could not be added
     * @throws SQLException
     */
    List<CompressedMap> addMaps(List<CompressedMap> maps) throws SQLException {
        List<CompressedMap> failed = new ArrayList<>();
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                for (CompressedMap map : maps) {
                    try {
                        statement.setInt(1, map.getId());
                        statement.setInt(2, map.getHash());
                        statement.setBytes(3, map.getCompressedMap());
                    } catch (Exception e) {
                        failed.add(map);
                        ArtMap.instance().getLogger().log(Level.SEVERE, String.format("Error writing map %s to database!", map.getId()),e);
                        continue;
                    }
                    statement.addBatch();
                }
            }
        }.executeBatch("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
        return failed;
    }
}
