package src;

import src.Board.CircuitBoard;
import src.Board.ParallelBoard;
import src.Board.SeriesBoard;
import src.Component.*;
import src.Utils.ConnectionLogic;
import src.Utils.GuiUtils;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class CircuitGame extends Application {

    private CircuitBoard board;
    private GridPane gridView;
    private Label statusLabel;

    private TextField valueInput;
    private TextArea propertiesArea;
    private boolean isSimulationSuccess = false;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // Menu
        MenuBar menuBar = new MenuBar();
        Menu menuGame = new Menu("Game");
        MenuItem itemSelectLevel = new MenuItem("Select Level");
        itemSelectLevel.setOnAction(e -> showLevelSelectDialog());
        MenuItem itemExit = new MenuItem("Exit");
        itemExit.setOnAction(e -> primaryStage.close());
        menuGame.getItems().addAll(itemSelectLevel, new SeparatorMenuItem(), itemExit);
        menuBar.getMenus().add(menuGame);
        
        Button btnRun = new Button("▶ Run Circuit");
        btnRun.setStyle("-fx-base: #90ee90; -fx-font-weight: bold;");
        btnRun.setOnAction(e -> runCircuitSimulation());

        Button btnReset = new Button("↺ Reset Level");
        btnReset.setOnAction(e -> resetLevel());

        ToolBar toolbar = new ToolBar(btnRun, new Separator(), btnReset);
        
        VBox topContainer = new VBox(menuBar, toolbar);
        root.setTop(topContainer);

        // Toolbox
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f4f4f4;");
        sidebar.setMinWidth(200);

        Label toolsHeader = new Label("Toolbox");
        toolsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label valueLabel = new Label("Set Value (Ω or F):");
        valueInput = new TextField();
        valueInput.setPromptText("Enter value here...");

        Label iconWire = createDraggableIcon(ComponentType.WIRE, Color.BLACK);
        Label iconCorner = createDraggableIcon(ComponentType.CORNER_WIRE, Color.BLACK);
        iconCorner.setText("Corner");
        Label iconTWire = createDraggableIcon(ComponentType.T_WIRE, Color.BLACK);
        iconTWire.setText("T-Junction");
        Label iconResistor = createDraggableIcon(ComponentType.RESISTOR, Color.ORANGE);
        Label iconCapacitor = createDraggableIcon(ComponentType.CAPACITOR, Color.GREEN);

        Label propHeader = new Label("Result");
        propHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");
        propertiesArea = new TextArea();
        propertiesArea.setEditable(false);
        propertiesArea.setPrefRowCount(10);
        propertiesArea.setWrapText(true);
        propertiesArea.setText("1. Select Level\n2. Set Value\n3. Drag components.");

        sidebar.getChildren().addAll(
                toolsHeader,
                valueLabel, valueInput,
                new Separator(),
                iconWire, iconCorner, iconTWire, iconResistor, iconCapacitor,
                new Separator(),
                propHeader, propertiesArea
        );

        // Workspace
        gridView = new GridPane();
        gridView.setAlignment(Pos.CENTER);
        gridView.setHgap(5);
        gridView.setVgap(5);
        
        ScrollPane gridScroll = new ScrollPane(gridView);
        gridScroll.setFitToWidth(true);
        gridScroll.setFitToHeight(true);
        gridScroll.setStyle("-fx-background-color: transparent;");

        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(gridScroll, sidebar);
        splitPane.setDividerPositions(0.75);
        
        root.setCenter(splitPane);

        statusLabel = new Label("Welcome! Select a level to start.");
        statusLabel.setPadding(new Insets(5));
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 950, 700);
        primaryStage.setTitle("Circuit Puzzle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Logic

    private void showLevelSelectDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select Level");
        alert.setHeaderText("Choose Puzzle Type");

        ButtonType btnSeries = new ButtonType("Series");
        ButtonType btnParallel = new ButtonType("Parallel");
        ButtonType btnCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(btnSeries, btnParallel, btnCancel);

        alert.showAndWait().ifPresent(type -> {
            if (type == btnSeries) {
                board = new SeriesBoard();
                statusLabel.setText("Loaded: Series Level");
            } else if (type == btnParallel) {
                board = new ParallelBoard();
                statusLabel.setText("Loaded: Parallel Level");
            }

            if (board != null) {
                isSimulationSuccess = false;
                propertiesArea.setText("Goal: Light the bulb for exactly 5.0 seconds.");
                updateGridDisplay();
            }
        });
    }

    private void resetLevel() {
        if (board != null) {
            board.clearGrid();
            isSimulationSuccess = false;
            updateGridDisplay();
            statusLabel.setText("Level Reset.");
            propertiesArea.setText("Board cleared.");
        }
    }

    private void runCircuitSimulation() {
        if (board == null) return;

        // Get valid path
        java.util.List<Component> activePath = board.getValidPath();

        if (activePath == null) {
            statusLabel.setText("Simulation Failed: Open Circuit!");
            isSimulationSuccess = false;
            updateGridDisplay();
            return;
        }

        // Calculate R and C on found path
        double totalResistance = board.calculateTotalResistance(activePath);
        double totalCapacitance = board.calculateTotalCapacitance(activePath);

        double tau = totalResistance * totalCapacitance;
        double duration = 5.0 * tau;

        // Report Results
        StringBuilder sb = new StringBuilder();
        sb.append("--- Connected Path Found ---\n");
        sb.append(String.format("Active Resistance: %.2f Ω\n", totalResistance));
        sb.append(String.format("Active Capacitance: %.3f F\n", totalCapacitance));
        sb.append("--------------------------\n");
        sb.append(String.format("Time Constant (τ): %.3f s\n", tau));
        sb.append(String.format("Bulb Duration: %.2f s\n", duration));

        // Win Condition
        if (Math.abs(duration - 5.0) < 0.1 && totalResistance > 0) {    // Duration around 5 with error <= 0.1 and No short Circuit
            sb.append("\nSUCCESS! Target Reached.");
            isSimulationSuccess = true;
            statusLabel.setText("Success! Circuit Valid.");
            
            for(Component c : activePath){
                if(c instanceof Bulb) ((Bulb)c).setLit(true);
            }

            updateGridDisplay();
            
            Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
            winAlert.setHeaderText("Puzzle Solved!");
            winAlert.setContentText("Great job! The bulb stays lit for 5 seconds.");
            winAlert.show();
        } else {
            isSimulationSuccess = false;
            if (totalCapacitance == 0) sb.append("\nFAILED. No connected Capacitor.");
            else if (totalResistance == 0) sb.append("\nFAILED. Short circuit.");
            else sb.append("\nFAILED. Timing incorrect.");

            statusLabel.setText("Simulation Failed.");
            updateGridDisplay(); 
        }

        propertiesArea.setText(sb.toString());
    }

    private Label createDraggableIcon(ComponentType type, Color color) {
        String rawName = type.name();
        String displayName = rawName.charAt(0) + rawName.substring(1).toLowerCase().replace('_', ' ');
        
        Label icon = new Label(displayName);
        
        icon.setPadding(new Insets(10));
        icon.setPrefWidth(180);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");
        icon.setTextFill(color.darker());
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Rectangle indicator = new Rectangle(10, 10, color);
        icon.setGraphic(indicator);

        icon.setOnDragDetected(event -> {
            boolean needsValue = (type == ComponentType.RESISTOR || type == ComponentType.CAPACITOR);
            String valText = valueInput.getText().trim();

            if (needsValue) {
                if (valText.isEmpty()) {
                    statusLabel.setText("Error: Enter a value for " + displayName + " first!");
                    valueInput.requestFocus();
                    event.consume();
                    return;
                }
                if (valText.isEmpty()) {
                    statusLabel.setText("Error: Enter a value first!");
                    event.consume();
                    return;
                }
                try { Double.parseDouble(valText); } 
                catch (NumberFormatException e) { return; }
            }

            Dragboard db = icon.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            if (valText.isEmpty()) valText = "0.0";
            
            content.putString(type.name() + ":" + valText);
            
            db.setContent(content);

            javafx.scene.SnapshotParameters snapParams = new javafx.scene.SnapshotParameters();
            snapParams.setFill(Color.TRANSPARENT);
            db.setDragView(icon.snapshot(snapParams, null));

            event.consume();
        });

        return icon;
    }

    private void updateGridDisplay() {
        gridView.getChildren().clear();
        if (board == null) return;

        int rows = board.getRows();
        int cols = board.getCols();

        // Store cell references for the connection 
        StackPane[][] cellNodes = new StackPane[rows][cols];

        // Create cells and components in toolbox
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                StackPane cell = new StackPane();
                cell.setMinSize(60, 60);
                cell.setPrefSize(60, 60);
                cell.setMaxSize(60, 60);
                
                StackPane contentContainer = new StackPane();
                
                Rectangle bg = new Rectangle(60, 60);
                bg.setStroke(Color.BLACK);
                bg.setStrokeType(StrokeType.INSIDE);

                Component comp = board.getComponent(r, c);

                if (comp == null) {
                    bg.setFill(Color.WHITE);
                    contentContainer.getChildren().add(bg);
                } else {
                    if (comp instanceof Source) {
                        bg.setFill(Color.RED);
                    } else if (comp instanceof Destination) {
                        bg.setFill(Color.BLUE);
                    } else if (comp instanceof Resistor) {
                        bg.setFill(Color.ORANGE);
                    } else if (comp instanceof Bulb) {
                        if (isSimulationSuccess) {
                            bg.setFill(Color.YELLOW);
                            bg.setEffect(new javafx.scene.effect.Glow(0.8));
                        } else {
                            bg.setFill(Color.DARKKHAKI);
                            bg.setEffect(null);
                        }
                    } else if (comp instanceof Capacitor) {
                        bg.setFill(Color.GREEN);
                    } else if (comp instanceof Wire || comp instanceof CornerWire || comp instanceof TWire) {
                        bg.setFill(Color.BLACK);
                    } else if (comp instanceof Block) {
                        bg.setFill(Color.DARKGRAY);
                        bg.setStroke(Color.GRAY);
                    }
                    
                    javafx.scene.Node directionNode = null;

                    // Corner wire (L-shaped, no arrow)
                    if (comp instanceof CornerWire) {
                        Pane visualPane = new Pane();
                        visualPane.setPrefSize(60, 60);

                        javafx.scene.shape.Polyline path = new javafx.scene.shape.Polyline(
                            30.0, 60.0,
                            30.0, 30.0,
                            60.0, 30.0
                        );
                        path.setStroke(Color.WHITE);
                        path.setStrokeWidth(4);
                        path.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

                        visualPane.getChildren().add(path);
                        visualPane.setOpacity(0.9);
                        directionNode = visualPane;
                    } 
                    // T-Wire (T-shaped, no arrow)
                    else if (comp instanceof TWire) {
                         Pane visualPane = new Pane();
                         visualPane.setPrefSize(60, 60);

                         javafx.scene.shape.Line horz = new javafx.scene.shape.Line(0, 30, 60, 30);
                         horz.setStroke(Color.WHITE);
                         horz.setStrokeWidth(4);
                         horz.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

                         javafx.scene.shape.Line vert = new javafx.scene.shape.Line(30, 30, 30, 60);
                         vert.setStroke(Color.WHITE);
                         vert.setStrokeWidth(4);
                         vert.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);

                         visualPane.getChildren().addAll(horz, vert);
                         visualPane.setOpacity(0.9);
                         directionNode = visualPane;
                    }
                    // Straight Wire (Line, no arrow)
                    else if (comp instanceof Wire || comp instanceof Resistor || comp instanceof Capacitor) {
                        javafx.scene.shape.Line line = new javafx.scene.shape.Line(0, 30, 60, 30);
                        line.setStroke(Color.WHITE);
                        line.setStrokeWidth(4);
                        line.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
                        line.setOpacity(0.9);
                        directionNode = line;
                    }
                    // Source (Arrow for direction since this only has 1 output port)
                    else if (comp instanceof Source) {
                        javafx.scene.shape.Polygon arrow = new javafx.scene.shape.Polygon();
                        arrow.getPoints().addAll(-10.0, -5.0, -10.0, 5.0, 10.0, 0.0);
                        arrow.setFill(Color.WHITE);
                        arrow.setOpacity(0.7);
                        directionNode = arrow;
                    }

                    if (directionNode != null) {
                        contentContainer.getChildren().addAll(bg, directionNode);
                    } else {
                        contentContainer.getChildren().add(bg);
                    }

                    // Rotation & Hover mouse for info
                    contentContainer.setRotate(comp.getRotationDegree());

                    String info = comp.getName(); 
                    if (comp instanceof Resistor) {
                        info += "\nResistance: " + ((Resistor)comp).getResistance() + " Ω";
                    } else if (comp instanceof Capacitor) {
                        info += "\nCapacitance: " + ((Capacitor)comp).getCapacitance() + " F";
                    } else if (comp instanceof Bulb) {
                        info += "\nState: " + (((Bulb)comp).isLit() ? "ON" : "OFF");
                    } else if (comp instanceof Source) {
                        info += "\nVoltage: " + ((Source)comp).getVoltage() + " V";
                    }

                    Tooltip tip = new Tooltip(info);
                    tip.setStyle("-fx-font-size: 14px; -fx-background-color: #222; -fx-text-fill: white;");
                    tip.setShowDelay(javafx.util.Duration.millis(50)); 
                    Tooltip.install(cell, tip);
                }

                cell.getChildren().add(contentContainer);

                // Handle events
                final int finalR = r;
                final int finalC = c;

                // Drag
                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                });

                // Drop
                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        String[] data = db.getString().split(":");
                        String type = data[0];
                        double val = (data.length > 1) ? Double.parseDouble(data[1]) : 0.0;
                        handleDrop(finalR, finalC, type, val);
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                // Click (left for rotate, right for remove)
                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        handleRemove(finalR, finalC);
                    } 
                    else if (event.getButton() == MouseButton.PRIMARY && comp != null) {
                        if (comp.canRotate()) { 
                            comp.rotate();
                            updateGridDisplay();
                        } else {
                            // Shake effect for non-rotatable items (Blocks)
                            statusLabel.setText("Cannot rotate this component!");
                            TranslateTransition shake = new TranslateTransition(Duration.millis(50), cell);
                            shake.setByX(5); 
                            shake.setCycleCount(4); 
                            shake.setAutoReverse(true); 
                            shake.play();
                        }
                    }
                });

                cellNodes[r][c] = cell;
                gridView.add(cell, c, r);
            }
        }

        // Visualize connections
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Component current = board.getComponent(r, c);
                if (current == null) continue;

                // Check right
                if (c + 1 < cols) {
                    Component right = board.getComponent(r, c + 1);
                    if (ConnectionLogic.areConnected(current, right, 0, 1)) {
                        GuiUtils.addConnectionMarker(cellNodes[r][c], 32, 0);
                    }
                }

                // Check bottom
                if (r + 1 < rows) {
                    Component bottom = board.getComponent(r + 1, c);
                    if (ConnectionLogic.areConnected(current, bottom, 1, 0)) {
                        GuiUtils.addConnectionMarker(cellNodes[r][c], 0, 32);
                    }
                }
            }
        }
    }
    
    private void handleDrop(int r, int c, String dataString, double value) {
        ComponentType type;
        try {
            type = ComponentType.valueOf(dataString);
        } catch (IllegalArgumentException e) {
            System.out.println("Unknown component type: " + dataString);
            return;
        }

        // Check component limits
        if (!board.canAdd(type)) {
            statusLabel.setText("Limit Reached for " + type.name());
            return;
        }

        Component newComp = null;
        
        switch(type) {
            case WIRE:        
                newComp = new Wire("Wire"); 
                break;
            case CORNER_WIRE: 
                newComp = new CornerWire("Corner"); 
                break;
            case T_WIRE:
                newComp = new TWire("Junction");
                break;
            case RESISTOR:    
                newComp = new Resistor("Resistor", value); 
                break;
            case CAPACITOR:   
                newComp = new Capacitor("Capacitor", value); 
                break;
            default:
                statusLabel.setText("Cannot place " + type.name());
                return;
        }
        if (board.placeComponent(r, c, newComp)) {
            statusLabel.setText("Placed " + type.name());
            updateGridDisplay();
        } else {
            statusLabel.setText("Cannot place here!");
        }
    }

    private void handleRemove(int r, int c) {
        if (board.removeComponent(r, c)) {
            statusLabel.setText("Removed component.");
            updateGridDisplay();
        } else {
            statusLabel.setText("Cannot remove! (Locked)");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}