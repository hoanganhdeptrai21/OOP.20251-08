package OOPLab20251.Board;

import OOPLab20251.Component.*;

import java.util.ArrayList;
import java.util.List;

public class ParallelBoard extends CircuitBoard {

    public ParallelBoard() {
        super(5, 6);
        this.maxResistors = 1;
        this.maxCapacitors = 2;
    }

    @Override
    protected void presetComponent() {

        Source parallelSource = new Source("OOPLab20251.Component.Source", 10.0);
        Destination parallelDestination = new Destination("Ground");
        Bulb parallelBulb = new Bulb("OOPLab20251.Component.Bulb");
        Block parallelBlock1 = new Block("parallelBlock1");
        Block parallelBlock2 = new Block("parallelBlock2");
        Block parallelBlock3 = new Block("parallelBlock3");
        Block parallelBlock4 = new Block("parallelBlock4");
        Block parallelBlock5 = new Block("parallelBlock5");
        Block parallelBlock6 = new Block("parallelBlock6");
        Block parallelBlock7 = new Block("parallelBlock7");
        Block parallelBlock8 = new Block("parallelBlock8");
        Block parallelBlock9 = new Block("parallelBlock9");
        Block parallelBlock10 = new Block("parallelBlock10");
        Block parallelBlock11= new Block("parallelBlock11");
        Wire parallelWire1 = new Wire("ParallelWire1");
        Wire parallelWire2 = new Wire("ParallelWire2");
        Wire parallelWire3 = new Wire("ParallelWire3");
        Wire parallelWire4 = new Wire("ParallelWire4");
        Wire parallelWire5 = new Wire("ParallelWire5");
        Wire parallelWire6 = new Wire("ParallelWire6");
        Wire parallelWire7 = new Wire("ParallelWire7");
        Wire parallelWire8 = new Wire("ParallelWire8");
        Wire parallelWire9 = new Wire("ParallelWire9");

        parallelSource.setLocked(true);
        parallelDestination.setLocked(true);
        parallelBulb.setLocked(true);
        parallelWire1.setLocked(true);
        parallelWire2.setLocked(true);
        parallelWire3.setLocked(true);
        parallelWire4.setLocked(true);
        parallelWire5.setLocked(true);
        parallelWire6.setLocked(true);
        parallelWire7.setLocked(true);
        parallelWire8.setLocked(true);
        parallelWire9.setLocked(true);

        placeComponent(2, 0, parallelSource);
        placeComponent(4, 5, parallelDestination);
        placeComponent(2, 5, parallelBulb);
        placeComponent(0, 0, parallelBlock1);
        placeComponent(1, 0, parallelBlock2);
        placeComponent(3, 0, parallelBlock3);
        placeComponent(4, 0, parallelBlock4);
        placeComponent(1, 2, parallelBlock5);
        placeComponent(2, 2, parallelBlock6);
        placeComponent(3, 2, parallelBlock7);
        placeComponent(1, 4, parallelBlock8);
        placeComponent(2, 4, parallelBlock9);
        placeComponent(3, 4, parallelBlock10);
        placeComponent(4, 4, parallelBlock11);
        placeComponent(1,1, parallelWire1);
        placeComponent(2,1, parallelWire2);
        placeComponent(3,1, parallelWire3);
        placeComponent(2,3, parallelWire4);
        placeComponent(3,3, parallelWire5);
        placeComponent(4,3, parallelWire6);
        placeComponent(0,4, parallelWire7);
        placeComponent(1,5, parallelWire8);
        placeComponent(3,5, parallelWire9);
    }

    @Override
    public double calculateTotalResistance() {
        List<Resistor> resistors = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Component comp = getComponent(r, c);
                if (comp instanceof Resistor) {
                    resistors.add((Resistor) comp);
                }
            }
        }

        if (resistors.isEmpty()) {
            return 0.0;
        }
        if (resistors.size() == 1) {
            return resistors.get(0).getResistance();
        }

        double inverseSum = 0.0;

        for (Resistor r : resistors) {
            double val = r.getResistance();
            if (val > 0) {
                inverseSum += (1.0 / val);
            }
        }

        if (inverseSum == 0) return 0.0;

        return 1.0 / inverseSum;
    }

    public double calculateTotalCapacitance() {
        List<Capacitor> parallelCapacitors = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] instanceof Capacitor) {
                    parallelCapacitors.add((Capacitor) grid[r][c]);
                }
            }
        }

        if (parallelCapacitors.isEmpty()) return 0.0;

        double sum = 0.0;
        for (Capacitor c : parallelCapacitors) {
            sum += c.getCapacitance();
        }

        return sum;
    }
}