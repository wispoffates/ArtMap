package me.Fupery.ArtMap.Painting;

public class CachedPixel {
    public final int x, y;
    public final Byte dye;

    public CachedPixel(int x, int y, byte dye) {
        this.x = x;
        this.y = y;
        this.dye = dye;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte getDye() {
        return dye;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CachedPixel)) return false;
        CachedPixel cachedPixel = (CachedPixel) obj;
        return cachedPixel.x == x && cachedPixel.y == y && cachedPixel.dye.equals(dye);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}