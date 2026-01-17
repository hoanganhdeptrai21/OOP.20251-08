package src.Utils;

import src.Board.CircuitBoard;
import src.Component.*;

public class ConnectionLogic {

    public static boolean[] getActivePorts(Component c) {
        boolean[] ports = {false, false, false, false}; // Top, Right, Bottom, Left, inactive by default
        if (c == null) return ports;

        int rot = c.getRotationDegree() % 360;

        if (c instanceof CornerWire) {
            if (rot == 0)        { ports[1]=true; ports[2]=true; }
            else if (rot == 90)  { ports[2]=true; ports[3]=true; }
            else if (rot == 180) { ports[3]=true; ports[0]=true; }
            else if (rot == 270) { ports[0]=true; ports[1]=true; }
        } else if (c instanceof Source) {
            if (rot == 0)        { ports[1]=true;}
            else if (rot == 90)  { ports[2]=true;}
            else if (rot == 180) { ports[3]=true;}
            else if (rot == 270) { ports[0]=true;}
        } else if (c instanceof Destination || c instanceof Bulb) {
            ports[0]=true; ports[1]=true; ports[2]=true; ports[3]=true;
        }
        else if (c instanceof TWire) {
            if (rot == 0)       { ports[1]=true; ports[2]=true; ports[3]=true; }
            else if (rot == 90) { ports[2]=true; ports[3]=true; ports[0]=true; }
            else if (rot == 180){ ports[3]=true; ports[0]=true; ports[1]=true; }
            else if (rot == 270){ ports[0]=true; ports[1]=true; ports[2]=true; }
        }
        else if (c instanceof Wire || c instanceof Resistor || c instanceof Capacitor) {
            if (rot == 0 || rot == 180) {
                ports[1] = true; ports[3] = true;
            } else {
                ports[0] = true; ports[2] = true;
            }
        }

        return ports;
    }

    public static boolean areConnected(Component source, Component target, int dRow, int dCol) {
        if (source == null || target == null) return false;

        // 1. Determine Source Direction Index (Top, Right, Bottom, Left)
        int srcDirIndex = -1;
        if (dRow == -1 && dCol == 0) srcDirIndex = 0;      // Top
        else if (dRow == 0 && dCol == 1) srcDirIndex = 1;  // Right
        else if (dRow == 1 && dCol == 0) srcDirIndex = 2;  // Bottom
        else if (dRow == 0 && dCol == -1) srcDirIndex = 3; // Left

        if (srcDirIndex == -1) return false;

        // 2. Determine Target Direction Index (Opposite Side)
        int tgtDirIndex = (srcDirIndex + 2) % 4;

        // 3. Check if both components have active ports in the required directions
        boolean[] srcPorts = getActivePorts(source);
        boolean[] tgtPorts = getActivePorts(target);

        // 4. Return connection status
        return srcPorts[srcDirIndex] && tgtPorts[tgtDirIndex];
    }

    public static int getFlowCount(CircuitBoard board, Component c) {
        if (c == null || board == null) return 0;

        // 1. Find the component's position
        int rows = board.getRows();
        int cols = board.getCols();
        int r = -1, col = -1;

        outer: for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board.getComponent(i, j) == c) {
                    r = i; col = j; break outer;
                }
            }
        }

        if (r == -1) return 0; // Not found

        int count = 0;
        // Direction vectors: Top, Bottom, Left, Right
        int[][] dirs = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] d : dirs) {
            int nr = r + d[0];
            int nc = col + d[1];

            // Check if neighbor is within bounds
            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                Component neighbor = board.getComponent(nr, nc);

                if (neighbor != null) {
                    // 2. Determine "My" Direction Index (0=Top, 1=Right, 2=Bottom, 3=Left)
                    int myDirIndex = -1;
                    if (d[0] == -1) myDirIndex = 0;      // Top
                    else if (d[0] == 1) myDirIndex = 2;  // Bottom
                    else if (d[1] == -1) myDirIndex = 3; // Left
                    else if (d[1] == 1) myDirIndex = 1;  // Right

                    // 3. Determine "Neighbor's" Required Port (Opposite Side)
                    // If I look Up (0), I need their Down (2) port.
                    int neighborDirIndex = (myDirIndex + 2) % 4;

                    // 4. Check Connection
                    boolean[] myPorts = getActivePorts(c);
                    boolean[] neighborPorts = getActivePorts(neighbor);

                    if (myPorts[myDirIndex] && neighborPorts[neighborDirIndex]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }
}