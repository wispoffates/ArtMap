package me.Fupery.ArtMap.Painting.Brushes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CachedPixel;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import me.Fupery.ArtMap.api.Colour.ArtDye;
import me.Fupery.ArtMap.api.Colour.Palette;

public class Dye extends Brush {
    private ArrayList<CachedPixel> dirtyPixels;

    private Palette palette = ArtMap.instance().getDyePalette();

    public Dye(CanvasRenderer renderer, Player player) {
        super(renderer, player);
        this.dirtyPixels = new ArrayList<>();
    }

    @Override
    public List<CachedPixel> paint(BrushAction action, ItemStack brush, long strokeTime) {
        ArtDye dye = palette.getDye(brush);
        if (dye == null) {
            return dirtyPixels;
        }
        if (action == BrushAction.LEFT_CLICK) {
            clean();
            byte[] pixel = getCurrentPixel();
            if (pixel != null) {
                dye.apply(getPixelAt(pixel[0], pixel[1]));
            }
        } else {
            if (strokeTime > 250) {
                clean();
            }
            byte[] pixel = getCurrentPixel();

            if (pixel != null) {

                if (dirtyPixels.size() > 0) {

                    CachedPixel lastFlowPixel = dirtyPixels.get(dirtyPixels.size() - 1);

                    if (lastFlowPixel.getDye() != this.resultColor(dye, pixel)) {
                        clean();
                    } else {
                        flowBrush(lastFlowPixel.getX(), lastFlowPixel.getY(), pixel[0], pixel[1], dye);
                        dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], this.resultColor(dye, pixel)));
                        return dirtyPixels;
                    }
                }
                dye.apply(getPixelAt(pixel[0], pixel[1]));
                dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], this.resultColor(dye, pixel)));
            }
        }
        return dirtyPixels;
    }

    private byte resultColor(ArtDye dye, byte[] pixel) {
        return dye.getDyeColour(this.getPixel(pixel[0], pixel[1]));
    }

    private byte resultColor(ArtDye dye, int x, int y) {
        return dye.getDyeColour(this.getPixel(x, y));
    }
 
    @Override
    public boolean checkMaterial(ItemStack brush) {
        return palette.getDye(brush) != null;
    }

    @Override
    public void clean() {
        dirtyPixels.clear();
    }

    private void flowBrush(int x, int y, int x2, int y2, ArtDye dye) {

        int w = x2 - x;
        int h = y2 - y;

        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;

        if (w != 0) {
            dx1 = (w > 0) ? 1 : -1;
            dx2 = (w > 0) ? 1 : -1;
        }

        if (h != 0) {
            dy1 = (h > 0) ? 1 : -1;
        }

        int longest = Math.abs(w);
        int shortest = Math.abs(h);

        if (!(longest > shortest)) {
            longest = Math.abs(h);
            shortest = Math.abs(w);

            if (h < 0) {
                dy2 = -1;

            } else if (h > 0) {
                dy2 = 1;
            }
            dx2 = 0;
        }
        int numerator = longest >> 1;

        for (int i = 0; i <= longest; i++) {
            if (!dirtyPixels.contains(new CachedPixel(x, y, this.resultColor(dye, x,y)))) {
                dye.apply(getPixelAt(x, y));
            }
            numerator += shortest;

            if (!(numerator < longest)) {
                numerator -= longest;
                x += dx1;
                y += dy1;

            } else {
                x += dx2;
                y += dy2;
            }
        }
    }
}
