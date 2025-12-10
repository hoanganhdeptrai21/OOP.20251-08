import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;

public class CircuitGame extends Application {

    private CircuitBoard board;
    private GridPane gridView;
    private Label statusLabel;

    // UI Logic State
    private String selectedTool = "Wire";
    private TextField valueInput;
    private Label valueLabel;
    private TextArea propertiesArea; // To "View Component Properties"

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();

        // --- 1. Top Menu Bar (Start, Save, Exit, Tutorial) ---
        MenuBar menuBar = new MenuBar();

        // Game Menu
        Menu menuGame = new Menu("Game");
        MenuItem itemStart = new MenuItem("Start New Game"); // "Start Game"
        itemStart.setOnAction(e -> showLevelSelectDialog());

        MenuItem itemSave = new MenuItem("Save Progress"); // "Save Progress"
        itemSave.setOnAction(e -> saveProgress());

        MenuItem itemExit = new MenuItem("Exit Game"); // "Exit Game"
        itemExit.setOnAction(e -> primaryStage.close());

        menuGame.getItems().addAll(itemStart, itemSave, new SeparatorMenuItem(), itemExit);

        // Help Menu
        Menu menuHelp = new Menu("Help");
        MenuItem itemTutorial = new MenuItem("Play Tutorial"); // "Play Tutorial"
        itemTutorial.setOnAction(e -> playTutorial());

        menuHelp.getItems().add(itemTutorial);

        menuBar.getMenus().addAll(menuGame, menuHelp);

        // --- 2. Action Toolbar (Run, Reset) ---
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #ddd; -fx-border-color: #ccc;");
        toolbar.setAlignment(Pos.CENTER_LEFT);

        Button btnRun = new Button("▶ Run Circuit"); // "Run Circuit" -> "View Result"
        btnRun.setStyle("-fx-background-color: #90ee90; -fx-font-weight: bold;");
        btnRun.setOnAction(e -> runCircuitSimulation());

        Button btnReset = new Button("↺ Reset Level"); // "Reset Level"
        btnReset.setOnAction(e -> resetLevel());

        toolbar.getChildren().addAll(btnRun, btnReset);

        // Combine Menu and Toolbar for Top
        VBox topContainer = new VBox(menuBar, toolbar);
        root.setTop(topContainer);

        // --- 3. Right Sidebar: Toolbox & Properties ---
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(15));
        sidebar.setStyle("-fx-background-color: #f4f4f4;");
        sidebar.setPrefWidth(200);

        // -- Toolbox Section --
        Label toolsHeader = new Label("Toolbox"); // "View Toolbox"
        toolsHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        ToggleGroup group = new ToggleGroup();
        RadioButton radioWire = new RadioButton("Wire");
        radioWire.setUserData("Wire");
        radioWire.setToggleGroup(group);
        radioWire.setSelected(true);

        RadioButton radioResistor = new RadioButton("Resistor");
        radioResistor.setUserData("Resistor");
        radioResistor.setToggleGroup(group);

        RadioButton radioCapacitor = new RadioButton("Capacitor");
        radioCapacitor.setUserData("Capacitor");
        radioCapacitor.setToggleGroup(group);

        // Input Field
        valueLabel = new Label("Value:");
        valueInput = new TextField();
        valueInput.setPromptText("Enter amount");
        valueInput.setDisable(true);

        group.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTool = newVal.getUserData().toString();
                updateInputVisibility();
            }
        });

        // -- Properties Section ("View Component Properties") --
        Label propHeader = new Label("Component Properties");
        propHeader.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 0 0;");

        propertiesArea = new TextArea();
        propertiesArea.setEditable(false);
        propertiesArea.setPrefRowCount(5);
        propertiesArea.setWrapText(true);
        propertiesArea.setText("Select a component on the grid to view details.");

        sidebar.getChildren().addAll(
                toolsHeader, radioWire, radioResistor, radioCapacitor,
                new Separator(), valueLabel, valueInput,
                new Separator(), propHeader, propertiesArea
        );
        root.setRight(sidebar);

        // --- 4. Center: Grid ---
        gridView = new GridPane();
        gridView.setAlignment(Pos.CENTER);
        gridView.setHgap(5);
        gridView.setVgap(5);
        root.setCenter(gridView);

        // --- 5. Bottom: Status ---
        statusLabel = new Label("Welcome! Select 'Game' -> 'Start New Game' to begin.");
        statusLabel.setPadding(new Insets(5));
        root.setBottom(statusLabel);

        // Initial Load (Default to Easy for immediate testing)
        loadLevel(CircuitBoard.Type.easy);

        Scene scene = new Scene(root, 900, 650);
        primaryStage.setTitle("Circuit Puzzle Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // --- USE CASE IMPLEMENTATIONS ---

    private void showLevelSelectDialog() {
        // "Select Level" Use Case
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select Level");
        alert.setHeaderText("Choose your difficulty:");

        ButtonType easyBtn = new ButtonType("Easy");
        ButtonType medBtn = new ButtonType("Medium");
        ButtonType hardBtn = new ButtonType("Hard");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(easyBtn, medBtn, hardBtn, cancelBtn);

        alert.showAndWait().ifPresent(type -> {
            if (type == easyBtn) loadLevel(CircuitBoard.Type.easy);
            else if (type == medBtn) loadLevel(CircuitBoard.Type.medium);
            else if (type == hardBtn) loadLevel(CircuitBoard.Type.hard);
        });
    }

    private void loadLevel(CircuitBoard.Type difficulty) {
        board = new CircuitBoard(difficulty);
        statusLabel.setText("Loaded Level: " + difficulty);
        propertiesArea.setText("Level Loaded.\nTarget: Connect Source to Ground.");
        updateGridDisplay();
    }

    private void resetLevel() {
        // "Reset Level" Use Case
        if (board != null) {
            board.clearGrid(); // This calls the method we wrote earlier that clears & re-presets
            updateGridDisplay();
            statusLabel.setText("Level Reset.");
            propertiesArea.setText("Board cleared.");
        }
    }

    private void saveProgress() {
        // "Save Progress" Use Case
        // In a real app, this would write to a file.
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Save Game");
        alert.setHeaderText(null);
        alert.setContentText("Progress Saved Successfully! (Mock)");
        alert.showAndWait();
    }

    private void playTutorial() {
        // "Play Tutorial" Use Case
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tutorial");
        alert.setHeaderText("How to Play");
        alert.setContentText("1. Select a component from the Toolbox.\n" +
                "2. Enter a value (if needed).\n" +
                "3. Click on the grid to Place Component.\n" +
                "4. Right-click to Remove.\n" +
                "5. Click 'Run Circuit' to test!");
        alert.showAndWait();
    }

    private void runCircuitSimulation() {
        // "Run Circuit" & "View Circuit Result" Use Case
        if (board == null) return;

        // Simple logic: Is the Source connected to Destination?
        // (In a full app, you would use DFS/BFS here)
        boolean isComplete = Math.random() > 0.5; // MOCK result for now

        String resultText;
        if (isComplete) {
            resultText = "SUCCESS! \nCurrent flow detected.\nVoltage stable.";
            statusLabel.setText("Simulation Result: Success");
        } else {
            resultText = "FAILURE. \nCircuit is open.\nCheck your connections.";
            statusLabel.setText("Simulation Result: Failed");
        }

        // Show result in properties box
        propertiesArea.setText(resultText);

        // Also pop up a dialog for the result
        Alert alert = new Alert(isComplete ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setTitle("Circuit Result");
        alert.setHeaderText("Simulation Complete");
        alert.setContentText(resultText);
        alert.showAndWait();
    }

    private void updateInputVisibility() {
        switch (selectedTool) {
            case "Wire":
                valueLabel.setText("Value:");
                valueInput.clear();
                valueInput.setDisable(true);
                break;
            case "Resistor":
                valueLabel.setText("Resistance (Ω):");
                valueInput.setDisable(false);
                break;
            case "Capacitor":
                valueLabel.setText("Capacitance (F):");
                valueInput.setDisable(false);
                break;
        }
    }

    private void updateGridDisplay() {
        gridView.getChildren().clear();

        // Auto-detect size
        int rows = 3, cols = 3;
        try { if (board.placeComponent(6, 0, new Wire("T"))) { rows = 7; cols = 7; board.removeComponent(6,0); }
        else if (board.placeComponent(4, 0, new Wire("T"))) { rows = 5; cols = 5; board.removeComponent(4,0); }
        } catch(Exception e) {}

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Rectangle cell = new Rectangle(50, 50); // Slightly smaller to fit UI
                cell.setStroke(Color.BLACK);
                cell.setStrokeType(StrokeType.INSIDE);

                Component comp = board.getComponent(r, c);

                if (comp == null) cell.setFill(Color.WHITE);
                else if (comp instanceof Source) cell.setFill(Color.RED);
                else if (comp instanceof Destination) cell.setFill(Color.BLUE);
                else if (comp instanceof Resistor) cell.setFill(Color.ORANGE);
                else if (comp instanceof Capacitor) cell.setFill(Color.GREEN);
                else if (comp instanceof Wire) cell.setFill(Color.BLACK);
                else cell.setFill(Color.GRAY);

                final int finalR = r;
                final int finalC = c;

                cell.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        // "Place Component"
                        // Or if already placed, "View Component Properties"
                        if (comp != null) {
                            showComponentProperties(comp);
                        } else {
                            handlePlace(finalR, finalC);
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        handleRemove(finalR, finalC);
                    }
                });

                gridView.add(cell, c, r);
            }
        }
    }

    private void showComponentProperties(Component comp) {
        // "View Component Properties" Use Case
        StringBuilder details = new StringBuilder();
        details.append("Type: ").append(comp.getClass().getSimpleName()).append("\n");
        details.append("Name: ").append(comp.getName()).append("\n");

        if (comp instanceof Resistor) {
            details.append("Resistance: ").append(((Resistor)comp).getResistance()).append(" Ω\n");
        } else if (comp instanceof Capacitor) {
            // Assuming you added getCapacitance() to Capacitor class
            details.append("Capacitor Data\n");
        } else if (comp instanceof Source) {
            details.append("Voltage: ").append(comp.getVoltage()).append(" V\n");
        }

        details.append("Current: ").append(comp.getCurrent()).append(" A");

        propertiesArea.setText(details.toString());
    }

    private void handlePlace(int r, int c) {
        Component newComp = null;
        double value = 0.0;

        if (!selectedTool.equals("Wire")) {
            try {
                value = Double.parseDouble(valueInput.getText());
            } catch (NumberFormatException e) {
                statusLabel.setText("Error: Invalid number!");
                return;
            }
        }

        switch (selectedTool) {
            case "Wire": newComp = new Wire("Wire"); break;
            case "Resistor": newComp = new Resistor("Resistor", value); break;
            case "Capacitor": newComp = new Capacitor("Cap", value); break;
        }

        if (board.placeComponent(r, c, newComp)) {
            statusLabel.setText("Placed " + selectedTool);
            updateGridDisplay();
            showComponentProperties(newComp); // Show properties immediately
        } else {
            statusLabel.setText("Cannot place here!");
        }
    }

    private void handleRemove(int r, int c) {
        if (board.removeComponent(r, c)) {
            statusLabel.setText("Removed component.");
            propertiesArea.setText("Component removed.");
            updateGridDisplay();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}