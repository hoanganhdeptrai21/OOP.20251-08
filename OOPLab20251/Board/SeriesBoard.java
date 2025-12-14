package OOPLab20251.Board;

import OOPLab20251.Component.*;

import java.util.ArrayList;
import java.util.List;

public class SeriesBoard extends CircuitBoard {

    public SeriesBoard() {
        super(3, 7);
        this.maxResistors = 2;
        this.maxCapacitors = 1;
    }

    public double calculateTotalResistance(){
        double sum = 0.0;
        for (int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j] instanceof Resistor){
                    sum += ((Resistor) grid[i][j]).getResistance();
                }
            }
        }
        return sum;
    }

    @Override
    protected void presetComponent() {
        Source seriesSource = new Source("OOPLab20251.Component.Source", 10.0);
        Destination seriesDestination = new Destination("Ground");
        Bulb seriesBulb = new Bulb("OOPLab20251.Component.Bulb");
        Wire seriesWire1 = new Wire("OOPLab20251.Component.Wire");
        Block seriesBlock1 = new Block("seriesBlock1");
        Block seriesBlock2 = new Block("seriesBlock2");
        Block seriesBlock3 = new Block("seriesBlock3");
        Block seriesBlock4 = new Block("seriesBlock4");
        Block seriesBlock5 = new Block("seriesBlock5");
        Block seriesBlock6 = new Block("seriesBlock6");
        Block seriesBlock7 = new Block("seriesBlock7");
        Block seriesBlock8 = new Block("seriesBlock8");

        seriesSource.setLocked();
        seriesDestination.setLocked();
        seriesBulb.setLocked();
        seriesWire1.setLocked();

        placeComponent(0, 0, seriesSource);
        placeComponent(0, 6, seriesBulb);
        placeComponent(2, 6, seriesDestination);
        placeComponent(1, 6, seriesWire1);
        placeComponent(1, 0, seriesBlock1);
        placeComponent(1, 1, seriesBlock2);
        placeComponent(2, 0, seriesBlock3);
        placeComponent(2, 1, seriesBlock4);
        placeComponent(0, 3, seriesBlock5);
        placeComponent(1, 3, seriesBlock6);
        placeComponent(1, 5, seriesBlock7);
        placeComponent(2, 5, seriesBlock8);
    }

    public double calculateTotalCapacitance() {
        List<Capacitor> seriesCapacitors = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] instanceof Capacitor) {
                    seriesCapacitors.add((Capacitor) grid[r][c]);
                }
            }
        }

        if (seriesCapacitors.isEmpty()) return 0.0;
        if (seriesCapacitors.size() == 1) return seriesCapacitors.get(0).getCapacitance();

        double inverseSum = 0.0;
        for (Capacitor c : seriesCapacitors) {
            double val = c.getCapacitance();
            if (val > 0) {
                inverseSum += (1.0 / val);
            }
        }

        if (inverseSum == 0) return 0.0;
        return 1.0 / inverseSum;
    }
}