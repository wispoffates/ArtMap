package me.Fupery.ArtMap.Painting;

import java.util.Random;

import me.Fupery.ArtMap.IO.PixelTableManager;

public class CursorTest {

    private static int resolutionFactor = 1;

    public static void main(String[] args) {
            //North facing = 0
            Cursor cursor = new Cursor(0,PixelTableManager.buildTables(null,resolutionFactor));

            //worst case scenario cursor is jumping all over the place (not realistic)
            int[] pitch = new Random().ints(10000000, -15, 15).toArray();
            int[] yaw = new Random().ints(10000000, -15, 15).toArray();
            System.out.println("Worst case scenario: Cursor moving randomly all over the screen.");
            long currentTime = System.currentTimeMillis();
            for(int i=0; i<pitch.length; i++) {
                cursor.setPitch(pitch[i]);
                cursor.setYaw(yaw[i]);
            }
            System.out.printf("Current method: %d operations in %d ms%n", pitch.length, System.currentTimeMillis()-currentTime);
            currentTime = System.currentTimeMillis();
            for(int i=0; i<pitch.length; i++) {
                doMath(pitch[i],yaw[i]);
                doMath(pitch[i],yaw[i]); //do the math twice sene I didn't copy both methods
            }
            System.out.printf("Trig method: %d operations in %d ms%n", pitch.length, System.currentTimeMillis()-currentTime);

            //best case scenario cursor is moving slowly (the usual painting)
            pitch = new Random().ints(10000000, -1, 1).toArray();
            yaw = new Random().ints(10000000, -1, 1).toArray();
            System.out.println("Best case scenario: Cursor moving slowing while painting.");
            currentTime = System.currentTimeMillis();
            for(int i=0; i<pitch.length; i++) {
                cursor.setPitch(pitch[i]);
                cursor.setYaw(yaw[i]);
            }
            System.out.printf("Current method: %d operations in %d ms%n", pitch.length, System.currentTimeMillis()-currentTime);
            currentTime = System.currentTimeMillis();
            for(int i=0; i<pitch.length; i++) {
                doMath(pitch[i],yaw[i]);
                doMath(pitch[i],yaw[i]); //do the math twice sene I didn't copy both methods
            }
            System.out.printf("Trig method: %d operations in %d ms%n", pitch.length, System.currentTimeMillis()-currentTime);
    }

    private static void doMath(int yaw, int pitch) {
        float playerToCanvasHorizontalDistance = 0.6480925F; //If you come up with a good way to calculate this at runtime, please implement that.
        float playerToCanvasVerticalDistance = -0.00287597F; //If you come up with a good way to calculate this at runtime, please implement that.
        int y=0;

        float physicalY = (float)(Math.tan(pitch)/Math.cos(yaw)*playerToCanvasHorizontalDistance + playerToCanvasVerticalDistance); //This is the coordinate within canvas' plane, with the middle of canvas being the coordinate origin
        y = (int)Math.floor( (physicalY + 0.5)*128/resolutionFactor );
        y = clampCoordinate(y);
    }

    private static int clampCoordinate(int val){
        int resolutionFactor = 4;
        int limit = (128 / resolutionFactor) - 1;
        if(val>limit) return limit;
        if(val<0) return 0;
        return val;
    }
    
}
