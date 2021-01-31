package me.Fupery.ArtMap.api.Painting;

import java.io.IOException;
import java.sql.SQLException;

import org.bukkit.entity.Player;

public interface IArtSession {

    boolean start(Player player) throws SQLException, IOException;
    void updatePosition(float yaw, float pitch);
    /**
     * @return True if the artsession has the artkit in use.
     */
    public boolean isInArtKit();
    void end(Player player) throws SQLException, IOException;
    public void persistMap(boolean resetRenderer) throws SQLException, IOException, NoSuchFieldException,
            IllegalAccessException ;
    boolean isActive();
}
