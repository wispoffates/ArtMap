package me.Fupery.DataTables;

import java.io.Serializable;

public class PixelTable implements Serializable{

    private int resolutionFactor;
    private float[] yawBounds;
    private Object[] pitchBounds;

    public PixelTable(int resolutionFactor, float[] yawBounds, Object[] pitchBounds) {
        this.resolutionFactor = resolutionFactor;
        this.yawBounds = yawBounds;
        this.pitchBounds = pitchBounds;
    }

    public int getResolutionFactor() {
        return resolutionFactor;
    }

    public float[] getYawBounds() {
        return yawBounds;
    }

    public Object[] getPitchBounds() {
        return pitchBounds;
    }
}
