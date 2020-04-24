package me.Fupery.ArtMap.Exception;

public class HeadFetchException extends ArtMapException {

    /**
     *  Thrown when there is an error fetching heads.
     */
    private static final long serialVersionUID = 1L;

    public HeadFetchException(String msg) {
        super(msg);
    }

    public HeadFetchException(String msg, Throwable e) {
        super(msg,e);
    }

}