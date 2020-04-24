package me.Fupery.ArtMap.IO.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import me.Fupery.ArtMap.ArtMap;

public class SQLiteTable {
    protected static final String sqlError = "Database error,";

    protected final SQLiteDatabase manager;
    protected final String TABLE;
    protected final String creationSQL;

    protected SQLiteTable(SQLiteDatabase database, String TABLE, String creationSQL) {
        this.manager = database;
        this.TABLE = TABLE;
        this.creationSQL = creationSQL;
    }

    protected void create() throws SQLException {
        manager.getLock().lock();
        try (Connection connection = manager.getConnection(); Statement buildTableStatement = connection.createStatement()) {
            buildTableStatement.executeUpdate(creationSQL);
        } finally {
            manager.getLock().unlock();
        }
    }

    protected abstract class QueuedStatement extends ArtTable.QueuedQuery<Void> {

        protected int[] executeBatch(String query) throws SQLException {
            Connection connection = null;
            PreparedStatement statement = null;
            int[] result = new int[0];

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = statement.executeBatch();
            } catch (SQLException e) {
				throw e;
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return result;
        }

        @Override
		protected Void read(ResultSet set) throws SQLException {
            return null;
        }

        @Override
		public Void execute(String query) throws SQLException {
            Connection connection = null;
            PreparedStatement statement = null;

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw e;
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return null;
        }
    }

    protected abstract class QueuedQuery<T> {

        protected abstract void prepare(PreparedStatement statement) throws SQLException;

        protected abstract T read(ResultSet set) throws SQLException;

        protected void close(Connection connection, PreparedStatement statement) {
            if (statement != null) try {
                statement.close();
            } catch (SQLException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, "Failure!", e);
            }
        }

        public T execute(String query) throws SQLException {
            Connection connection = null;
            PreparedStatement statement = null;
            T result = null;

            manager.getLock().lock();
            try {
                connection = manager.getConnection();
                statement = connection.prepareStatement(query);
                prepare(statement);
                result = read(statement.executeQuery());
            } catch (SQLException e) {
                throw e;
            } finally {
                close(connection, statement);
                manager.getLock().unlock();
            }
            return result;
        }
    }
}
