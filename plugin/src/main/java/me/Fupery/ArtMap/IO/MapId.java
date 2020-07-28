package me.Fupery.ArtMap.IO;

public class MapId {
    protected final int id;
    protected final Integer hash;

    public MapId(int id, int hash) {
        this.id = id;
        this.hash = hash;
    }

    public int getId() {
        return id;
    }

    public Integer getHash() {
        return hash;
    }
}
