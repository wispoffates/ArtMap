package me.Fupery.ArtMap.api.Painting;

public class Pixel {
    private final int x, y;
    private ICanvasRenderer canvas;
    private byte colour;

    public Pixel(ICanvasRenderer canvas, int x, int y, byte colour) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        this.colour = colour;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public byte getColour() {
        return colour;
    }

    public void setColour(byte colour) {
        canvas.addPixel(x, y, colour);
        this.colour = colour;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pixel)) return false;
        Pixel pixel = (Pixel) obj;
        return pixel.x == x && pixel.y == y && pixel.colour == colour;
    }

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
