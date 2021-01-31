package me.Fupery.ArtMap.Painting.Brushes;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.api.Config.Lang;
import me.Fupery.ArtMap.Painting.Brush;
import me.Fupery.ArtMap.Painting.CachedPixel;
import me.Fupery.ArtMap.Painting.CanvasRenderer;
import me.Fupery.ArtMap.api.Colour.ArtDye;

public class Dropper extends Brush {
    private ArrayList<CachedPixel> dirtyPixels;
    private Byte dye = null;

    public Dropper(CanvasRenderer renderer, Player player) {
        super(renderer, player);
        this.dirtyPixels = new ArrayList<>();
    }

    @Override
    public List<CachedPixel> paint(BrushAction action, ItemStack brush, long strokeTime) {
        if (action == BrushAction.LEFT_CLICK) {
            clean();
            byte[] pixel = getCurrentPixel();
            if (pixel != null) {
                dye = getPixelAt(pixel[0], pixel[1]).getColour();
                ArtDye artdye =  ArtMap.instance().getDyePalette().getDye(dye);
                Bukkit.getScheduler().runTask(ArtMap.instance(), ()->{
                    this.player.sendMessage(Lang.ITEM_SPONGE_MESSAGE.get() + ": " + artdye.name() + " :: " + artdye.getMaterial().name() + " :: " + dye);
                });
            }
        } else {
            if (dye == null) {
                return dirtyPixels;
            }
            if (strokeTime > 250) {
                clean();
            }
            byte[] pixel = getCurrentPixel();

            if (pixel != null) {

                if (dirtyPixels.size() > 0) {

                    CachedPixel lastFlowPixel = dirtyPixels.get(dirtyPixels.size() - 1);

                    if (lastFlowPixel.getDye() != dye) {
                        clean();
                    } else {
                        flowBrush(lastFlowPixel.getX(), lastFlowPixel.getY(), pixel[0], pixel[1], dye);
                        dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], dye));
                        return dirtyPixels;
                    }
                }
                getPixelAt(pixel[0], pixel[1]).setColour(dye);;
                dirtyPixels.add(new CachedPixel(pixel[0], pixel[1], dye));
            }
        }
        return dirtyPixels;
    }

    public Byte getColour() { 
        return this.dye;
    }
 
    @Override
    public boolean checkMaterial(ItemStack brush) {
        return brush.getType() == Material.SPONGE;
    }

    @Override
    public void clean() {
        dirtyPixels.clear();
    }

    private void flowBrush(int x, int y, int x2, int y2, Byte dye) {

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
            if (!dirtyPixels.contains(new CachedPixel(x, y, dye))) {
                getPixelAt(x, y).setColour(dye);
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
