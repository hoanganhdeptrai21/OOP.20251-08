package OOPLab20251.Board;

import OOPLab20251.Component.*;

import java.util.ArrayList;
import java.util.List;

public abstract class CircuitBoard {
    protected int rows;
    protected int cols;
    protected Component[][] grid;
    protected List<Component> toolbox = new ArrayList<>();
    protected int maxResistors = -1;
    protected int maxCapacitors = -1;

    public CircuitBoard(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.grid = new Component[rows][cols];

        presetComponent();
    }

    protected abstract void presetComponent();

    public abstract double calculateTotalResistance();

    public abstract double calculateTotalCapacitance();

    public boolean canAdd(String type){
        if(type.equalsIgnoreCase("OOPLab20251.Component.Wire")) return true;
        int limit = -1;
        Class<?> target = null;
        if(type.equalsIgnoreCase("OOPLab20251.Component.Resistor")){
            limit = maxResistors;
            target = Resistor.class;
        }
        else if(type.equalsIgnoreCase("Capacitator")){
            limit = maxCapacitors;
            target = Capacitor.class;
        }
        if(limit == -1) return true;
        int current = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j] != null && target.isInstance(grid[i][j])){
                    current++;
                }
            }
        }
        return current < limit;
    }
    private boolean canConnect(Component comp, int rowDirection, int colDirection) {
        if (comp == null) return false;
        if (comp instanceof Block) return false;

        if (comp instanceof Source || comp instanceof Destination || comp instanceof Bulb) {
            return true;
        }

        int rot = comp.getRotationDegree() % 360;

        if (comp instanceof Wire || comp instanceof Resistor || comp instanceof Capacitor) {
            if (rot == 0 || rot == 180) {
                return (colDirection != 0 && rowDirection == 0);
            } else {
                return (rowDirection != 0 && colDirection == 0);
            }
        }

        if (comp instanceof CornerWire) {
            if (rot == 0) { // (└ shape)
                return (rowDirection == 1 && colDirection == 0) || (rowDirection == 0 && colDirection == 1);
            }
            else if (rot == 90) { //  (┘ shape)
                return (rowDirection == 1 && colDirection == 0) || (rowDirection == 0 && colDirection == -1);
            }
            else if (rot == 180) { //  (┐ shape)
                return (rowDirection == -1 && colDirection == 0) || (rowDirection == 0 && colDirection == -1);
            }
            else if (rot == 270) { //  (┌ shape)
                return (rowDirection == -1 && colDirection == 0) || (rowDirection == 0 && colDirection == 1);
            }
        }

        return false;
    }

    protected boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public Component getComponent(int row, int col) {
        if (!isValid(row, col)) return null;
        return grid[row][col];
    }

    public boolean placeComponent(int row, int col, Component component) {
        if (!isValid(row, col)) return false;
        if (grid[row][col] == null) {
            grid[row][col] = component;
            return true;
        }
        return false;
    }

    public boolean removeComponent(int row, int col) {
        if (!isValid(row, col)) return false;

        Component toRemove = grid[row][col];
        if (toRemove == null) return false;

        if (toRemove.isLocked()) return false;
        toolbox.add(toRemove);
        grid[row][col] = null;
        return true;
    }

    public void clearGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = null;
            }
        }
        presetComponent();
    }

    // Getters for GUI if needed
    public int getRows() { return rows; }
    public int getCols() { return cols; }
}