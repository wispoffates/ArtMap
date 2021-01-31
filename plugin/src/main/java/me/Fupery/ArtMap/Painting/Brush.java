package me.Fupery.ArtMap.Painting;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.api.Painting.Pixel;

public abstract class Brush {

    protected int cooldownMilli = 150;
    private CanvasRenderer canvas;
    protected Player player;

    protected Brush(CanvasRenderer canvas, Player player) {
        this.canvas = canvas;
        this.player = player;
    }

    /**
     * Paint with the provided brush.
     * @param action The brush action (Left or right click)
     * @param brush The object that is the brush.
     * @param strokeTime How long is the button held.
     * @return List of modified pixels.
     */
    public abstract List<CachedPixel> paint(BrushAction action, ItemStack brush, long strokeTime);

    public abstract boolean checkMaterial(ItemStack brush);

    public abstract void clean();

    protected int getCooldown() {
        return cooldownMilli;
    }

    protected void addPixel(int x, int y, byte colour) {
        canvas.addPixel(x, y, colour);
    }

    protected byte getPixel(int x, int y) {
        return canvas.getPixel(x, y);
    }

    protected byte[] getCurrentPixel() {
        return canvas.getCurrentPixel();
    }

    protected Pixel getPixelAt(int x, int y) {
        return canvas.getPixelAt(x, y);
    }

    protected boolean isOffCanvas() {
        return canvas.isOffCanvas();
    }

    protected byte[][] getPixelBuffer() {
        return canvas.getPixelBuffer();
    }

    protected int getAxisLength() {
        return canvas.getAxisLength();
    }

    protected enum BrushAction {
        LEFT_CLICK, RIGHT_CLICK
    }
}
