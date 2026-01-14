package src.Utils;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class GuiUtils {

    public static void addConnectionMarker(Pane parentCell, double xOffset, double yOffset) {
        Circle dot = new Circle(4, Color.YELLOW);
        dot.setStroke(Color.BLACK);
        dot.setStrokeWidth(1);
        
        if (xOffset != 0) dot.setTranslateX(xOffset);
        if (yOffset != 0) dot.setTranslateY(yOffset);
        
        // Move the dot to the top
        parentCell.getChildren().add(dot);
    }
}