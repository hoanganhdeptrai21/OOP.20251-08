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

public class CircuitGame extends Application {

    private CircuitBoard board;
    private GridPane gridView;
    private Label statusLabel;

    // UI State
    private TextField valueInput;
    private TextArea propertiesArea;
    private boolean isSimulationSuccess = false;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // --- 1. Top Menu & Toolbar ---
        MenuBar menuBar = new MenuBar();
        Menu menuGame = new Menu("Game");
        MenuItem itemSelectLevel = new MenuItem("Select Level");
        itemSelectLevel.setOnAction(e -> showLevelSelectDialog());
        MenuItem itemExit = new MenuItem("Exit");
        itemExit.setOnAction(e -> primaryStage.close());
        menuGame.getItems().addAll(itemSelectLevel, new SeparatorMenuItem(), itemExit);
        menuBar.getMenus().add(menuGame);

        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnRun = new Button("▶ Run Circuit");
        btnRun.setStyle("-fx-background-color: #90ee90; -fx-font-weight: bold;");
        btnRun.setOnAction(e -> runCircuitSimulation());

        Button btnReset = new Button("↺ Reset Level");
        btnReset.setOnAction(e -> resetLevel());

        toolbar.getChildren().addAll(btnRun, btnReset);
        root.setTop(new VBox(menuBar, toolbar));

        // --- 2. Right Sidebar (Toolbox) ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f4f4f4;");
        sidebar.setPrefWidth(220);

        Label toolsHeader = new Label("Toolbox (Drag items)");
        toolsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label valueLabel = new Label("Set Value (Ω or F):");
        valueInput = new TextField();
        valueInput.setPromptText("Enter value here...");

        // --- TOOLBOX ICONS ---
        // Bulb is REMOVED from here as requested
        Label iconWire = createDraggableIcon("Wire", Color.BLACK);
        Label iconResistor = createDraggableIcon("Resistor", Color.ORANGE);
        Label iconCapacitor = createDraggableIcon("Capacitor", Color.GREEN);

        Label propHeader = new Label("Properties / Result");
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
                iconWire, iconResistor, iconCapacitor, // No Bulb here
                new Separator(),
                propHeader, propertiesArea
        );
        root.setRight(sidebar);

        // --- 3. Center Grid ---
        gridView = new GridPane();
        gridView.setAlignment(Pos.CENTER);
        gridView.setHgap(5);
        gridView.setVgap(5);
        root.setCenter(gridView);

        // --- 4. Bottom Status ---
        statusLabel = new Label("Welcome! Select a level to start.");
        statusLabel.setPadding(new Insets(5));
        root.setBottom(statusLabel);

        Scene scene = new Scene(root, 950, 700);
        primaryStage.setTitle("Circuit Puzzle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- GAME LOGIC ---

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

        // 1. Get Resistance (Abstract method handles Series vs Parallel math)
        double totalResistance = board.calculateTotalResistance();

        // 2. Get Capacitance (Sum up all capacitors placed on board)
        double totalCapacitance = calculateTotalCapacitanceOnBoard();

        // 3. Calculate Timing
        // Tau = R * C
        double tau = totalResistance * totalCapacitance;

        // Duration = 5 * Tau
        double duration = 5.0 * tau;

        // 4. Report Results
        StringBuilder sb = new StringBuilder();
        sb.append("--- Simulation Results ---\n");
        sb.append(String.format("Resistance (R): %.2f Ω\n", totalResistance));
        sb.append(String.format("Capacitance (C): %.3f F\n", totalCapacitance));
        sb.append("--------------------------\n");
        sb.append(String.format("Time Constant (τ): %.3f s\n", tau));
        sb.append(String.format("Bulb Duration: %.2f s\n", duration));

        // 5. Win Condition (Target 5.0s)
        if (Math.abs(duration - 5.0) < 0.1 && totalResistance > 0) {
            sb.append("\nSUCCESS! Target Reached.");
            isSimulationSuccess = true;
            statusLabel.setText("Success! Circuit Valid.");
            updateGridDisplay(); // Bulb turns yellow

            Alert winAlert = new Alert(Alert.AlertType.INFORMATION);
            winAlert.setHeaderText("Puzzle Solved!");
            winAlert.setContentText("Great job! The bulb stays lit for 5 seconds.");
            winAlert.show();
        } else {
            isSimulationSuccess = false;
            if (totalCapacitance == 0) sb.append("\nFAILED. No Capacitor found.");
            else if (totalResistance == 0) sb.append("\nFAILED. Short circuit.");
            else sb.append("\nFAILED. Timing incorrect.");

            statusLabel.setText("Simulation Failed.");
            updateGridDisplay(); // Bulb turns off
        }

        propertiesArea.setText(sb.toString());
    }

    // Helper to sum capacitors found on the grid
    private double calculateTotalCapacitanceOnBoard() {
        double sum = 0.0;
        int rows = board.getRows();
        int cols = board.getCols();
        for(int r=0; r<rows; r++) {
            for(int c=0; c<cols; c++) {
                Component comp = board.getComponent(r, c);
                if(comp instanceof Capacitor) {
                    // Assuming you added getCapacitance() to Capacitor class
                    // If not, we assume the input value is stored in 'voltage' or similar,
                    // but ideally Capacitor has a dedicated field.
                    // casting to Capacitor to access getter:
                    sum += ((Capacitor)comp).getCapacitance();
                }
            }
        }
        return sum;
    }

    // --- GUI HELPERS ---

    private Label createDraggableIcon(String name, Color color) {
        Label icon = new Label(name);
        icon.setPadding(new Insets(10));
        icon.setPrefWidth(180);
        icon.setAlignment(Pos.CENTER);
        icon.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");
        icon.setTextFill(color.darker());
        icon.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        Rectangle indicator = new Rectangle(10, 10, color);
        icon.setGraphic(indicator);

        icon.setOnDragDetected(event -> {
            // Validation
            boolean needsValue = name.equals("Resistor") || name.equals("Capacitor");
            String valText = valueInput.getText().trim();

            if (needsValue) {
                if (valText.isEmpty()) {
                    statusLabel.setText("Error: Enter a value for " + name + " first!");
                    valueInput.requestFocus();
                    event.consume();
                    return;
                }
                try {
                    Double.parseDouble(valText);
                } catch (NumberFormatException e) {
                    statusLabel.setText("Error: Invalid number!");
                    event.consume();
                    return;
                }
            }

            Dragboard db = icon.startDragAndDrop(TransferMode.COPY);
            ClipboardContent content = new ClipboardContent();
            if (valText.isEmpty()) valText = "0.0";
            content.putString(name + ":" + valText);
            db.setContent(content);
            event.consume();
        });

        return icon;
    }

    private void updateGridDisplay() {
        gridView.getChildren().clear();
        if (board == null) return;

        int rows = board.getRows();
        int cols = board.getCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                StackPane cell = new StackPane();
                Rectangle bg = new Rectangle(60, 60);
                bg.setStroke(Color.BLACK);
                bg.setStrokeType(StrokeType.INSIDE);
                cell.getChildren().add(bg);

                Component comp = board.getComponent(r, c);

                if (comp == null) {
                    bg.setFill(Color.WHITE);
                } else if (comp instanceof Source) {
                    bg.setFill(Color.RED);
                } else if (comp instanceof Destination) {
                    bg.setFill(Color.BLUE);
                } else if (comp instanceof Resistor) {
                    bg.setFill(Color.ORANGE);
                } else if (comp instanceof Capacitor) {
                    bg.setFill(Color.GREEN);
                } else if (comp instanceof Wire) {
                    bg.setFill(Color.BLACK);
                } else if (comp instanceof Block) {
                    bg.setFill(Color.DARKGRAY); // Visual for Obstacles
                    bg.setStroke(Color.GRAY);
                } else if (comp instanceof Bulb) {
                    // Bulb Visual Logic
                    if (isSimulationSuccess) {
                        bg.setFill(Color.YELLOW);
                        bg.setEffect(new javafx.scene.effect.Glow(0.8));
                    } else {
                        bg.setFill(Color.DARKKHAKI);
                        bg.setEffect(null);
                    }
                }

                // --- DROP HANDLERS ---
                final int finalR = r;
                final int finalC = c;

                cell.setOnDragOver(event -> {
                    if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                        event.acceptTransferModes(TransferMode.COPY);
                    }
                    event.consume();
                });

                cell.setOnDragDropped(event -> {
                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasString()) {
                        String[] data = db.getString().split(":");
                        handleDrop(finalR, finalC, data[0], Double.parseDouble(data[1]));
                        success = true;
                    }
                    event.setDropCompleted(success);
                    event.consume();
                });

                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        handleRemove(finalR, finalC);
                    } else if (event.getButton() == MouseButton.PRIMARY && comp != null) {
                        showComponentProperties(comp);
                    }
                });

                gridView.add(cell, c, r);
            }
        }
    }

    private void handleDrop(int r, int c, String type, double value) {
        // --- NEW: INVENTORY CHECK ---
        // Checks if we have reached the Max Limit for this board
        if (!board.canAdd(type)) {
            statusLabel.setText("Limit Reached! Max " + type + "(s) used.");
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Inventory Limit");
            alert.setContentText("This level limits the number of " + type + "s you can use.");
            alert.show();
            return;
        }

        Component newComp = null;
        switch(type) {
            case "Wire": newComp = new Wire("Wire"); break;
            case "Resistor": newComp = new Resistor("Resistor", value); break;
            case "Capacitor": newComp = new Capacitor("Cap", value); break;
            // Bulb case removed since it is not draggable
        }

        if (board.placeComponent(r, c, newComp)) {
            statusLabel.setText("Placed " + type);
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

    private void showComponentProperties(Component comp) {
        String msg = "Type: " + comp.getClass().getSimpleName() + "\n";
        msg += "Name: " + comp.getName() + "\n";

        if (comp instanceof Resistor) msg += "R: " + ((Resistor)comp).getResistance() + " Ω\n";
        if (comp instanceof Capacitor) msg += "C: " + ((Capacitor)comp).getCapacitance() + " F\n";
        if (comp.isLocked()) msg += "[LOCKED COMPONENT]";

        propertiesArea.setText(msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}