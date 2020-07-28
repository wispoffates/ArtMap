package me.Fupery.ArtMap.IO.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.bukkit.Bukkit;

import me.Fupery.ArtMap.ArtMap;

public class SQLiteDatabase {
    protected final File dbFile;
    private Connection connection;
    private ReentrantLock connectionLock = new ReentrantLock(true);

    public SQLiteDatabase(File dbFile) {
        this.dbFile = dbFile;
    }

    protected Connection getConnection() {
        if (!dbFile.exists()) {
            try {
                if (!dbFile.createNewFile()) {
                    Bukkit.getLogger().warning(String.format("[ArtMap] Could not create '%s'!", dbFile.getAbsolutePath()));
                    return null;
                }
            } catch (IOException e) {
                ArtMap.instance().getLogger().log(Level.SEVERE, String.format("File write error: '%s'!", dbFile.getAbsolutePath()),e);
                return null;
            }
        }
        try {
            if (connection != null && !connection.isClosed()) {//todo
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException | ClassNotFoundException e) {
            connection = null;
			ArtMap.instance().getLogger().log(Level.SEVERE, String.format("File write error: '%s'!", dbFile.getAbsolutePath()),e);
        }
        return connection;
    }

    protected void initialize(SQLiteTable... tables) throws SQLException {
        connection = getConnection();
        for (SQLiteTable table : tables) {
            table.create();
        }
    }

    public ReentrantLock getLock() {
        return connectionLock;
    }
}
