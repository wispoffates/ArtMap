package me.Fupery.ArtMap.Exception;

/**
 * Generic ArtMap Exception
 * Should be sub classed to be useful but can be used in non-recoverable scenarios.
 */
public class ArtMapException extends Exception {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ArtMapException(String msg) {
        super(msg);
    }

    public ArtMapException(String msg, Throwable exception) {
        super(msg, exception);
    }
}