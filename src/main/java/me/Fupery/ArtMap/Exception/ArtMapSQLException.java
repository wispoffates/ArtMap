package me.Fupery.ArtMap.Exception;

/**
 * Thrown when a SQL Exception occurs.
 */
public class ArtMapSQLException extends ArtMapException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ArtMapSQLException(String msg) {
        super(msg);
    }

    public ArtMapSQLException(String msg, Throwable exception) {
        super(msg,exception);
    }

}