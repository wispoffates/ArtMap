package me.Fupery.DataTables;
public class Test {

    public static void main(String[] args) {
        PixelTable table;
        try {
            table = DataTables.loadTable(4);
        } catch (DataTables.InvalidResolutionFactorException e) {
            e.printStackTrace();
            return;
        }

        for (float f :table.getYawBounds()) {
            System.out.println(f);
        }
    }
}
