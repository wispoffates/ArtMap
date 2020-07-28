package me.Fupery.ArtMap.Exception;

/**
 * Thrown when an artmap operation cannot be completed because it
 * the player does not have permisison.
 */
public class PermissionException extends ArtMapException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PermissionException(String msg) {
        super(msg);
    }

    public PermissionException(String msg, Throwable exception) {
        super(msg,exception);
    }

}