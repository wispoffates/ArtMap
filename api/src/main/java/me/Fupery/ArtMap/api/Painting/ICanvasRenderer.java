package me.Fupery.ArtMap.api.Painting;

import org.bukkit.map.MapView;

public interface ICanvasRenderer {

    //adds pixel at location
    public void addPixel(int x, int y, byte colour);
    byte getPixel(int x, int y);
    //finds the corresponding pixel for the yaw & pitch clicked
    byte[] getCurrentPixel() ;
    byte[] getMap() ;
    void stop();
    public Pixel getPixelAt(int x, int y);
    boolean isDirty();
    boolean isOffCanvas();
    byte[][] getPixelBuffer();
    int getAxisLength();
    MapView getMapView();
}