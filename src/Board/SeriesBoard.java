package src.Board;

import src.Component.*;
import src.Utils.ConnectionLogic;

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
        Source Source = new Source("Source", 10.0);
        Destination Destination = new Destination("Ground");
        Bulb Bulb = new Bulb("Bulb");
        Block Block_1 = new Block("Block");
        Block Block_2 = new Block("Block");
        Block Block_3 = new Block("Block");
        Block Block_4 = new Block("Block");
        Block Block_5 = new Block("Block");
        Block Block_6 = new Block("Block");
        Block Block_7 = new Block("Block");
        Block Block_8 = new Block("Block");

        Source.setLocked(true);
        Destination.setLocked(true);
        Bulb.setLocked(true);

        placeComponent(0, 0, Source);
        placeComponent(0, 6, Bulb);
        placeComponent(2, 6, Destination);
        placeComponent(1, 0, Block_1);
        placeComponent(1, 1, Block_2);
        placeComponent(2, 0, Block_3);
        placeComponent(2, 1, Block_4);
        placeComponent(0, 3, Block_5);
        placeComponent(1, 3, Block_6);
        placeComponent(1, 5, Block_7);
        placeComponent(2, 5, Block_8);
    }

    @Override
    public double calculateTotalResistance(List<Component> activeComponents) {
        double sum = 0.0;
        for (int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                Component comp = grid[i][j];
                if(comp instanceof Resistor){
                    if (ConnectionLogic.getFlowCount(this, comp) >= 2) {
                        sum += ((Resistor) comp).getResistance();
                    }
                }
            }
        }
        return sum;
    }
    @Override
    public double calculateTotalCapacitance(List<Component> activeComponents) {
        List<Capacitor> caps = new ArrayList<>();
        for (Component comp : activeComponents) {
            if (comp instanceof Capacitor) {
                caps.add((Capacitor) comp);
            }
        }

        if (caps.isEmpty()) return 0.0;
        if (caps.size() == 1) return caps.get(0).getCapacitance();

        double inverseSum = 0.0;
        for (Capacitor c : caps) {
            double val = c.getCapacitance();
            if (val > 0) inverseSum += (1.0 / val);
        }

        if (inverseSum == 0) return 0.0;
        return 1.0 / inverseSum;
    }
}