package me.Fupery.ArtMap.Exception;

/**
 * Thrown when an artmap operation cannot be completed because it would
 * overwrite another artwork.
 */
public class DuplicateArtworkException extends ArtMapException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public DuplicateArtworkException(String msg) {
        super(msg);
    }

    public DuplicateArtworkException(String msg, Throwable exception) {
        super(msg,exception);
    }

}