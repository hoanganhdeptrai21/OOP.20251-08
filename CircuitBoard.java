import java.util.ArrayList;
import java.util.List;

public class CircuitBoard {
    private int rows;
    private int cols;
    private Component[][] grid;
    private List<Component> toolbox = new ArrayList<>();

    public enum Type {
        series,
        parallel
    }

    private Type type;

    public CircuitBoard(Type type) {
        this.type = type;
        switch (type){
            case series:
                rows = 3;
                cols = 7;
            case parallel:
                rows = 5;
                cols = 7;
            default:
                rows = 5;
                cols = 5;
        }
        this.grid = new Component[rows][cols];
        presetComponent(type);
    }

    private void presetComponent(Type type) {
        switch (type) {
            case series:
                Source src = new Source("Source", 10.0);
                Destination des = new Destination("Ground");
                Bulb bulb = new Bulb("Bulb");
                Capacitor capacitor = new Capacitor("Capacitor", 0.01);
                src.setLocked();
                des.setLocked();
                bulb.setLocked();
                capacitor.setLocked();

                placeComponent(0, 0, src);
                placeComponent(2, 6, des);
                placeComponent(0, 4, bulb);
                placeComponent(2, 4, capacitor);

                break;

            case parallel:
                Source parallelSrc = new Source("Source", 10.0);
                Capacitor parallelCapacitor = new Capacitor("Capacitor", 0.01);
                Bulb parallelBulb = new Bulb("Bulb");
                parallelSrc.setLocked();
                parallelCapacitor.setLocked();
                parallelBulb.setLocked();



        }
    }

    public double calculateTotalResistance(){

    }

    private boolean isValid(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public Component getComponent(int row, int col) {
        if (!isValid(row, col)) {
            return null;
        }
        return grid[row][col];
    }

    public boolean placeComponent(int row, int col, Component component) {
        if (!isValid(row, col)) {
            return false;
        }
        if (grid[row][col] == null){
            grid[row][col] = component;
            return true;
        }
        return false;
    }

    public boolean removeComponent(int row, int col) {
        if (!isValid(row, col)) {
            return false;
        }
        Component toRemove = grid[row][col];
        if(toRemove == null){
            return false;
        }
        if(toRemove instanceof Source ||  toRemove instanceof Destination){
            return false;
        }
        toolbox.add(getComponent(row, col));
        grid[row][col] = null;
        return true;
    }

    public void clearGrid() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = null;
            }
        }
        // Re-load the level presets after clearing
        presetComponent(this.difficulty);
    }
}