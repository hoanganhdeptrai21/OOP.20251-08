package src.Board;

import src.Component.*;
import src.Utils.ConnectionLogic;

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

        Source Source = new Source("Source", 10.0);
        Destination Destination = new Destination("Ground");
        Bulb Bulb = new Bulb("Bulb");
        Block Block_1 = new Block("Block_1");
        Block Block_2 = new Block("Block_2");
        Block Block_3 = new Block("Block_3");
        Block Block_4 = new Block("Block_4");
        Block Block_5 = new Block("Block_5");
        Block Block_6 = new Block("Block_6");
        Block Block_7 = new Block("Block_7");
        Block Block_8 = new Block("Block_8");
        Block Block_9 = new Block("Block_9");
        Block Block_10 = new Block("Block_10");
        Block Block_11= new Block("Block_11");

        Source.setLocked();
        Destination.setLocked();
        Bulb.setLocked();

        placeComponent(2, 0, Source);
        placeComponent(4, 5, Destination);
        placeComponent(2, 5, Bulb);
        placeComponent(0, 0, Block_1);
        placeComponent(1, 0, Block_2);
        placeComponent(3, 0, Block_3);
        placeComponent(4, 0, Block_4);
        placeComponent(1, 2, Block_5);
        placeComponent(2, 2, Block_6);
        placeComponent(3, 2, Block_7);
        placeComponent(1, 4, Block_8);
        placeComponent(2, 4, Block_9);
        placeComponent(3, 4, Block_10);
        placeComponent(4, 4, Block_11);
    }

    @Override
    public double calculateTotalResistance(List<Component> activeComponents) {
        List<Resistor> resistors = new ArrayList<>();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Component comp = getComponent(r, c);
                if (comp instanceof Resistor) {
                    // --- THE FIX ---
                    // Only count if it has flow (In AND Out)
                    // Disconnected resistors usually have 0 or 1 connection.
                    if (ConnectionLogic.getFlowCount(this, comp) >= 2) {
                        resistors.add((Resistor) comp);
                    }
                }
            }
        }

        if (resistors.isEmpty()) return 0.0;
        if (resistors.size() == 1) return resistors.get(0).getResistance();

        double inverseSum = 0.0;
        for (Resistor r : resistors) {
            double val = r.getResistance();
            if (val > 0) inverseSum += (1.0 / val);
        }

        if (inverseSum == 0) return 0.0;
        return 1.0 / inverseSum;
    }

    @Override
    public double calculateTotalCapacitance(List<Component> activeComponents) {
        double sum = 0.0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Component comp = getComponent(r, c);
                if (comp instanceof Capacitor) {
                     // --- THE FIX ---
                    if (ConnectionLogic.getFlowCount(this, comp) >= 2) {
                        sum += ((Capacitor) comp).getCapacitance();
                    }
                }
            }
        }
        return sum;
    }
}