package OOPLab20251.Board;

import OOPLab20251.Component.*;
import OOPLab20251.Utils.ConnectionLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

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

    public abstract double calculateTotalResistance(List<Component> activeComponents);
    public abstract double calculateTotalCapacitance(List<Component> activeComponents);

    protected static class Point{
        public int row;
        public int col;
        public Point(int row, int col){
            this.row = row;
            this.col = col;
        }
    }

    protected Point findComponentType(ComponentType type){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                Component component = grid[i][j];
                if(type.isInstance(component)){
                    return new Point(i, j);
                }
            }
        }
        return null;
    }
    
    public List<Component> getValidPath() {
        Point start = findComponentType(ComponentType.SOURCE);
        Point end = findComponentType(ComponentType.DESTINATION);

        if (start == null || end == null) return null;

        Queue<Point> queue = new LinkedList<>();
        queue.add(start);

        boolean[][] visited = new boolean[rows][cols];
        visited[start.row][start.col] = true;

        // List to store ALL components in the connected circuit
        List<Component> activePath = new ArrayList<>();
        if (grid[start.row][start.col] != null) {
            activePath.add(grid[start.row][start.col]);
        }

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        boolean destinationReached = false;

        while(!queue.isEmpty()){
            Point currentPoint = queue.poll();
            Component currentComponent = grid[currentPoint.row][currentPoint.col];

            if(currentPoint.row == end.row && currentPoint.col == end.col){
                destinationReached = true;
            }

            for(int[] direction: directions){
                int rowDirection = direction[0];
                int colDirection = direction[1];

                int nextRow = currentPoint.row + rowDirection;
                int nextCol = currentPoint.col + colDirection;

                if(isValid(nextRow, nextCol) && !visited[nextRow][nextCol]){
                    Component neighbor = grid[nextRow][nextCol];

                    // USE CONNECTION LOGIC (Delegated or Local)
                    if(canConnect(currentComponent, rowDirection, colDirection) && 
                        canConnect(neighbor, -rowDirection, -colDirection)){
                        
                        visited[nextRow][nextCol] = true;
                        queue.add(new Point(nextRow, nextCol));
                        activePath.add(neighbor); // Add to valid list
                    }
                }
            }
        }

        if (destinationReached) {
            return activePath;
        } else {
            return null; // Circuit broken
        }
    }
    
    public boolean canAdd(ComponentType type){
        if(type == ComponentType.WIRE) return true;
        int limit = -1;
        switch(type){
            case RESISTOR:
                limit = maxResistors;
                break;
            case CAPACITOR:
                limit = maxCapacitors;
                break;
            default:
                return true;
        }
        if(limit == -1) return true;
        int current = 0;
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                if(grid[i][j] != null && type.isInstance(grid[i][j])){
                    current++;
                }
            }
        }
        return current < limit;
    }

    private boolean canConnect(Component comp, int rowDirection, int colDirection) {
        if (comp == null || comp instanceof Block) return false;

        // 1. Convert Grid Direction (rowDir, colDir) to Port Index
        // 0=Top, 1=Right, 2=Bottom, 3=Left
        int directionIndex = -1;
        
        if (rowDirection == -1 && colDirection == 0) directionIndex = 0; // Up
        else if (rowDirection == 0 && colDirection == 1) directionIndex = 1; // Right
        else if (rowDirection == 1 && colDirection == 0) directionIndex = 2; // Down
        else if (rowDirection == 0 && colDirection == -1) directionIndex = 3; // Left

        if (directionIndex == -1) return false; // Should not happen

        // 2. Delegate to the Centralized Visual Logic
        // This ensures the physics ALWAYS matches the visual cyan dots.
        boolean[] allowedPorts = ConnectionLogic.getActivePorts(comp);
        
        return allowedPorts[directionIndex];
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