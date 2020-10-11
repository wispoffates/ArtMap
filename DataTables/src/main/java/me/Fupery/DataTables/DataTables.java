package me.Fupery.DataTables;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class DataTables {

    public static PixelTable loadTable(int resolutionFactor)
            throws InvalidResolutionFactorException{

        PixelTable pixelTable;

        if (resolutionFactor == 1
                || resolutionFactor == 2
                || resolutionFactor == 4) {

            String fileName = String.format("/table_%s.dat", resolutionFactor);

            InputStream pixelTables = DataTables.class.getResourceAsStream(fileName);

            if (pixelTables != null) {

                ObjectInputStream ois;
                GZIPInputStream gis;

                try {
                    gis = new GZIPInputStream(pixelTables);
                    ois = new ObjectInputStream(gis);

                    pixelTable = (PixelTable) ois.readObject();

                    gis.close();
                    ois.close();

                    return pixelTable;

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            } else {
                System.out.println("[ArtMap][DataTables] Table data could not be read.");
            }

        } else {
            throw new InvalidResolutionFactorException();
        }
        return null;
    }

    public static class InvalidResolutionFactorException extends Throwable { }
}
