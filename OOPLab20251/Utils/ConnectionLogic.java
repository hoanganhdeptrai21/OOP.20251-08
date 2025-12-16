package OOPLab20251.Utils;

import OOPLab20251.Board.CircuitBoard;
import OOPLab20251.Component.*;

public class ConnectionLogic {

    // Helper to determine active ports based on component type and rotation
    // Returns boolean[] { TOP, RIGHT, BOTTOM, LEFT }
    public static boolean[] getActivePorts(Component c) {
        boolean[] ports = {false, false, false, false}; // Default: No connections
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

    public static boolean areConnected(Component source, Component target, int direction) {
        if (source == null || target == null) return false;

        boolean[] srcPorts = getActivePorts(source);
        boolean[] tgtPorts = getActivePorts(target);

        if (direction == 1) { // Checking RIGHT neighbor
            // I need a Right port (1), Neighbor needs a Left port (3)
            return srcPorts[1] && tgtPorts[3];
        } 
        else if (direction == 2) { // Checking BOTTOM neighbor
            // I need a Bottom port (2), Neighbor needs a Top port (0)
            return srcPorts[2] && tgtPorts[0];
        }
        return false;
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