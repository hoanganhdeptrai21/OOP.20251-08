package OOPLab20251;

import OOPLab20251.Board.CircuitBoard;
import OOPLab20251.Board.ParallelBoard;
import OOPLab20251.Board.SeriesBoard;
import OOPLab20251.Component.*;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
public class CircuitGame extends Application {
    private CircuitBoard board;
    private boolean isSimulationSuccess = false;
    private BorderPane rootPane;
    private GridPane gridView;
    private TextArea propertiesArea;
    private Label toastLabel;
    private Label emptyBoardLabel;

    private TextField resistorField;
    private TextField capacitorField;
    private boolean darkMode = true;
    private static final String TOOL_WIRE = "WIRE";
    private static final String TOOL_RESISTOR = "RESISTOR";
    private static final String TOOL_CAPACITOR = "CAPACITOR";
    @Override
    public void start(Stage stage) {
        rootPane = new BorderPane();
        rootPane.setPadding(new Insets(14));
        rootPane.setTop(buildTopBar());
        rootPane.setLeft(buildToolbox());
        rootPane.setCenter(buildBoard());
        rootPane.setRight(buildProperties());
        rootPane.setBottom(buildToast());
        applyTheme();
        Scene scene = new Scene(rootPane, 1200, 720);
        stage.setTitle("Circuit Puzzle (Student Friendly UI)");
        stage.setScene(scene);
        stage.show();
        showToast("Bấm Select Level để bắt đầu!", false);
    }
    private Node buildTopBar() {
        HBox top = new HBox(10);
        top.setPadding(new Insets(12));
        top.setAlignment(Pos.CENTER_LEFT);
        top.setStyle(cardStyle());

        Label title = new Label("⚡ Circuit Puzzle");
        title.setFont(Font.font("Segoe UI", FontWeight.EXTRA_BOLD, 18));

        Button btnSelectLevel = pillButton("Select Level");
        btnSelectLevel.setOnAction(e -> showLevelDialog());

        Button btnRun = pillButton("▶ Run");
        btnRun.setOnAction(e -> runCircuitSimulation());

        Button btnReset = pillButton("↺ Reset");
        btnReset.setOnAction(e -> resetLevel());

        ToggleButton toggleTheme = new ToggleButton("🌙 Dark");
        toggleTheme.setSelected(true);
        toggleTheme.setOnAction(e -> {
            darkMode = toggleTheme.isSelected();
            toggleTheme.setText(darkMode ? "🌙 Dark" : "☀ Light");
            applyTheme();
        });
        styleToggle(toggleTheme);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        top.getChildren().addAll(title, btnSelectLevel, btnRun, btnReset, spacer, toggleTheme);
        top.setUserData(title);
        return top;
    }
    private Node buildToolbox() {
        VBox box = new VBox(12);
        box.setPadding(new Insets(12));
        box.setPrefWidth(280);
        box.setStyle(cardStyle());
        Label header = new Label("Toolbox");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        resistorField = new TextField();
        resistorField.setPromptText("Resistor (Ω) ví dụ 1000");
        styleInput(resistorField);
        capacitorField = new TextField();
        capacitorField.setPromptText("Capacitor (µF) ví dụ 1000");
        styleInput(capacitorField);
        Button wireBtn = toolButton("Wire", TOOL_WIRE);
        wireBtn.setGraphic(iconWire3D());
        Button resistorBtn = toolButton("Resistor", TOOL_RESISTOR);
        resistorBtn.setGraphic(iconResistor3D());
        Button capacitorBtn = toolButton("Capacitor", TOOL_CAPACITOR);
        capacitorBtn.setGraphic(iconCapacitor3D());
        Label hint = new Label("Kéo nút vào ô trên board.\nChuột phải: xóa.\nChuột trái: xem thông tin.");
        hint.setWrapText(true);
        hint.setOpacity(0.85);
        box.getChildren().addAll(header, resistorField, capacitorField, wireBtn, resistorBtn, capacitorBtn, new Separator(), hint);
        box.setUserData(new Node[]{header, hint});
        return box;
    }

    private Node buildBoard() {
        StackPane wrap = new StackPane();
        wrap.setPadding(new Insets(12));
        wrap.setStyle(cardStyle());

        gridView = new GridPane();
        gridView.setHgap(10);
        gridView.setVgap(10);
        gridView.setAlignment(Pos.CENTER);
        gridView.setPadding(new Insets(16));

        emptyBoardLabel = new Label("Chưa có level.\nBấm Select Level.");
        emptyBoardLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));

        wrap.getChildren().addAll(gridView, emptyBoardLabel);
        return wrap;
    }

    private Node buildProperties() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(12));
        box.setPrefWidth(340);
        box.setStyle(cardStyle());

        Label header = new Label("Properties / Result");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        propertiesArea = new TextArea();
        propertiesArea.setEditable(false);
        propertiesArea.setWrapText(true);
        propertiesArea.setPrefRowCount(18);
        propertiesArea.setText("""
                1) Select Level
                2) Nhập R (Ω) hoặc C (µF)
                3) Kéo thả linh kiện
                4) Run để kiểm tra mục tiêu 5.0s
                """.trim());

        box.getChildren().addAll(header, propertiesArea);
        box.setUserData(header);
        return box;
    }
    private Node buildToast() {
        HBox bar = new HBox();
        bar.setPadding(new Insets(8, 14, 0, 14));
        bar.setAlignment(Pos.CENTER_LEFT);

        toastLabel = new Label("");
        toastLabel.setOpacity(0);
        toastLabel.setTextFill(Color.WHITE);
        toastLabel.setStyle(
                "-fx-background-radius: 999;" +
                        "-fx-padding: 8 12;"
        );

        bar.getChildren().add(toastLabel);
        return bar;
    }

    private void applyTheme() {
        rootPane.setStyle(darkMode
                ? "-fx-background-color: #0b1220;"
                : "-fx-background-color: #f4f6f8;"
        );
        Label title = (Label) rootPane.getTop().getUserData();
        title.setTextFill(darkMode ? Color.web("#e5e7eb") : Color.web("#1f2937"));
        VBox toolbox = (VBox) rootPane.getLeft();
        Node[] toolboxNodes = (Node[]) toolbox.getUserData();
        ((Label) toolboxNodes[0]).setTextFill(darkMode ? Color.web("#e5e7eb") : Color.web("#111827"));
        ((Label) toolboxNodes[1]).setTextFill(darkMode ? Color.web("#9ca3af") : Color.web("#6b7280"));
        VBox props = (VBox) rootPane.getRight();
        Label propsHeader = (Label) props.getUserData();
        propsHeader.setTextFill(darkMode ? Color.web("#e5e7eb") : Color.web("#111827"));
        propertiesArea.setStyle(darkMode
                ? "-fx-control-inner-background: #0f172a;" +
                "-fx-text-fill: #e5e7eb;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: rgba(255,255,255,0.12);"
                : "-fx-control-inner-background: #ffffff;" +
                "-fx-text-fill: #111827;" +
                "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-border-color: rgba(0,0,0,0.10);"
        );
        gridView.setStyle(darkMode
                ? "-fx-background-color: #0f1b2e;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(255,255,255,0.10);"
                : "-fx-background-color: #eaf2ff;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(0,0,0,0.06);"
        );

        emptyBoardLabel.setTextFill(darkMode ? Color.web("#9ca3af") : Color.web("#6b7280"));
    }
    private void styleToggle(ToggleButton t) {
        t.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        t.setStyle(
                "-fx-background-radius: 999;" +
                        "-fx-border-radius: 999;" +
                        "-fx-background-color: " + (darkMode ? "#111827" : "#ffffff") + ";" +
                        "-fx-text-fill: " + (darkMode ? "#e5e7eb" : "#111827") + ";" +
                        "-fx-border-color: rgba(255,255,255,0.15);" +
                        "-fx-padding: 8 14;"
        );
    }
    private void showLevelDialog() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Select Level");
        a.setHeaderText("Choose Puzzle Type");

        ButtonType series = new ButtonType("Series");
        ButtonType parallel = new ButtonType("Parallel");
        a.getButtonTypes().setAll(series, parallel, ButtonType.CANCEL);
        a.showAndWait().ifPresent(type -> {
            if (type == series) {
                board = new SeriesBoard();
                showToast("Loaded: Series", false);
            } else if (type == parallel) {
                board = new ParallelBoard();
                showToast("Loaded: Parallel", false);
            } else {
                return;
            }
            isSimulationSuccess = false;
            updateGridDisplay();
        });
    }

    private void resetLevel() {
        if (board == null) {
            showToast("Chưa chọn level.", true);
            return;
        }
        board.clearGrid();
        isSimulationSuccess = false;
        updateGridDisplay();
        propertiesArea.setText("Board cleared.");
        showToast("Reset OK.", false);
    }
    private void runCircuitSimulation() {
        if (board == null) {
            showToast("Chưa chọn level.", true);
            return;
        }
        double totalR = board.calculateTotalResistance();
        double totalC = calculateTotalCapacitanceOnBoard(); // F
        double tau = totalR * totalC;
        double duration = 5.0 * tau;
        StringBuilder sb = new StringBuilder();
        sb.append("--- Simulation Results ---\n");
        sb.append(String.format("R: %.2f Ω\n", totalR));
        sb.append(String.format("C: %.2f µF\n", totalC * 1e6));
        sb.append(String.format("τ = R*C: %.4f s\n", tau));
        sb.append(String.format("Bulb Duration: %.2f s\n", duration));
        boolean ok = Math.abs(duration - 5.0) < 0.1 && totalR > 0 && totalC > 0;
        if (ok) {
            isSimulationSuccess = true;
            sb.append("\n\n✅ SUCCESS!");
            showToast("SUCCESS! đúng 5.0s", false);
        } else {
            isSimulationSuccess = false;
            sb.append("\n\n❌ FAILED.");
            showToast("FAILED. Thử lại.", true);
        }
        propertiesArea.setText(sb.toString());
        updateGridDisplay();
    }

    private double calculateTotalCapacitanceOnBoard() {
        double sum = 0.0;
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Component comp = board.getComponent(r, c);
                if (comp instanceof Capacitor cap) sum += cap.getCapacitance();
            }
        }
        return sum;
    }
    private void updateGridDisplay() {
        gridView.getChildren().clear();
        if (emptyBoardLabel != null) emptyBoardLabel.setVisible(board == null);
        if (board == null) return;
        int rows = board.getRows();
        int cols = board.getCols();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final int fr = r; // lambda final
                final int fc = c;
                StackPane cell = new StackPane();
                cell.setPrefSize(64, 64);
                Rectangle bg = new Rectangle(64, 64);
                bg.setArcWidth(16);
                bg.setArcHeight(16);
                bg.setStrokeWidth(1.2);
                if (darkMode) {
                    bg.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#1f2a44")),
                            new Stop(1, Color.web("#101a2f"))
                    ));
                    bg.setStroke(Color.rgb(255,255,255,0.10));
                } else {
                    bg.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                            new Stop(0, Color.web("#ffffff")),
                            new Stop(1, Color.web("#dfe6ee"))
                    ));
                    bg.setStroke(Color.rgb(0,0,0,0.08));
                }
                bg.setEffect(new DropShadow(6, Color.rgb(0,0,0,0.22)));
                Component comp = board.getComponent(fr, fc);
                applyComponentColor(bg, comp);
                cell.getChildren().add(bg);
                cell.setOnDragOver(ev -> {
                    if (ev.getGestureSource() != cell && ev.getDragboard().hasString()) {
                        ev.acceptTransferModes(TransferMode.COPY);
                    }
                    ev.consume();
                });
                cell.setOnDragDropped(ev -> {
                    Dragboard db = ev.getDragboard();
                    if (db.hasString()) {
                        String[] data = db.getString().split(":");
                        String type = data[0];
                        double value = Double.parseDouble(data[1]);
                        placeFromTool(fr, fc, type, value);
                        ev.setDropCompleted(true);
                    } else {
                        ev.setDropCompleted(false);
                    }
                    ev.consume();
                });

                cell.setOnMouseClicked(ev -> {
                    if (ev.getButton() == MouseButton.SECONDARY) {
                        handleRemove(fr, fc);
                    } else if (ev.getButton() == MouseButton.PRIMARY) {
                        Component current = board.getComponent(fr, fc);
                        if (current != null) showComponentProperties(current);
                    }
                });

                gridView.add(cell, fc, fr);
            }
        }
    }

    private void applyComponentColor(Rectangle bg, Component comp) {
        if (comp == null) return;
        if (comp instanceof Source) bg.setFill(Color.web("#ef4444"));
        else if (comp instanceof Destination) bg.setFill(Color.web("#3b82f6"));
        else if (comp instanceof Resistor) bg.setFill(Color.web("#f59e0b"));
        else if (comp instanceof Capacitor) bg.setFill(Color.web("#22c55e"));
        else if (comp instanceof Wire || comp instanceof CornerWire) bg.setFill(Color.web("#111827"));
        else if (comp instanceof Block) bg.setFill(Color.web("#6b7280"));
        else if (comp instanceof Bulb) {
            bg.setFill(isSimulationSuccess ? Color.web("#fde047") : Color.web("#9ca3af"));
            if (isSimulationSuccess) bg.setEffect(new DropShadow(18, Color.rgb(253, 224, 71, 0.55)));
        }
    }
    private void placeFromTool(int r, int c, String toolType, double value) {
        if (board == null) return;

        if (!board.canAdd(toolType)) {
            showToast("Limit reached: " + toolType, true);
            return;
        }
        Component newComp = null;
        if (TOOL_WIRE.equals(toolType)) {
            newComp = new Wire("Wire");
        } else if (TOOL_RESISTOR.equals(toolType)) {
            newComp = new Resistor("R", value); // Ω
        } else if (TOOL_CAPACITOR.equals(toolType)) {
            newComp = new Capacitor("C", value * 1e-6); // µF -> F
        }

        if (newComp == null) return;

        if (board.placeComponent(r, c, newComp)) {
            showToast("Placed: " + toolType, false);
            updateGridDisplay();
        } else {
            showToast("Cannot place here!", true);
        }
    }

    private void handleRemove(int r, int c) {
        if (board == null) return;

        if (board.removeComponent(r, c)) {
            showToast("Removed.", false);
            updateGridDisplay();
        } else {
            showToast("Cannot remove (Locked).", true);
        }
    }

    private void showComponentProperties(Component comp) {
        StringBuilder sb = new StringBuilder();
        sb.append("Type: ").append(comp.getClass().getSimpleName()).append("\n");
        sb.append("Name: ").append(comp.getName()).append("\n");

        if (comp instanceof Resistor r) sb.append(String.format("R: %.2f Ω\n", r.getResistance()));
        if (comp instanceof Capacitor cap) sb.append(String.format("C: %.2f µF\n", cap.getCapacitance() * 1e6));
        if (comp.isLocked()) sb.append("[LOCKED]");

        propertiesArea.setText(sb.toString());
    }

    private Button toolButton(String text, String typeId) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setPrefHeight(54);
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        b.setContentDisplay(ContentDisplay.LEFT);

        if (darkMode) {
            b.setTextFill(Color.web("#e5e7eb"));
            b.setStyle(
                    "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-background-color: linear-gradient(#121c33,#0d1426);" +
                            "-fx-border-color: rgba(255,255,255,0.12);" +
                            "-fx-border-width: 1.2;" +
                            "-fx-padding: 10 12;"
            );
        } else {
            b.setTextFill(Color.web("#111827"));
            b.setStyle(
                    "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-background-color: linear-gradient(#ffffff,#dfe6ee);" +
                            "-fx-border-color: rgba(0,0,0,0.10);" +
                            "-fx-border-width: 1.2;" +
                            "-fx-padding: 10 12;"
            );
        }
        b.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.20)));

        // Drag start
        b.setOnDragDetected(ev -> {
            if (board == null) {
                showToast("Chưa chọn level.", true);
                ev.consume();
                return;
            }
            String val = "0";
            if (TOOL_RESISTOR.equals(typeId)) {
                val = resistorField.getText().trim();
                if (!isValidNumber(val)) { showToast("Nhập R (Ω) hợp lệ!", true); ev.consume(); return; }
            }
            if (TOOL_CAPACITOR.equals(typeId)) {
                val = capacitorField.getText().trim();
                if (!isValidNumber(val)) { showToast("Nhập C (µF) hợp lệ!", true); ev.consume(); return; }
            }

            Dragboard db = b.startDragAndDrop(TransferMode.COPY);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(typeId + ":" + val);
            db.setContent(cc);
            ev.consume();
        });

        return b;
    }
    private Node iconWire3D() {
        // Dây cong + highlight + shadow
        Path wire = new Path(
                new MoveTo(6, 22),
                new CubicCurveTo(18, 6, 34, 38, 46, 22)
        );
        wire.setStrokeWidth(6);
        wire.setStrokeLineCap(StrokeLineCap.ROUND);
        wire.setFill(Color.TRANSPARENT);

        wire.setStroke(new LinearGradient(
                0,0,1,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0b0f16")),
                new Stop(1, Color.web("#2b3648"))
        ));

        Path highlight = new Path(
                new MoveTo(7, 20),
                new CubicCurveTo(18, 7, 34, 37, 45, 22)
        );
        highlight.setStrokeWidth(2.2);
        highlight.setStrokeLineCap(StrokeLineCap.ROUND);
        highlight.setFill(Color.TRANSPARENT);
        highlight.setStroke(Color.rgb(255,255,255,0.35));

        StackPane icon = new StackPane(wire, highlight);
        icon.setPrefSize(54, 34);
        icon.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.35)));
        return icon;
    }

    private Node iconResistor3D() {

        Line leftLeg = new Line(6, 18, 16, 18);
        Line rightLeg = new Line(44, 18, 54, 18);
        leftLeg.setStroke(Color.web("#94a3b8"));
        rightLeg.setStroke(Color.web("#94a3b8"));
        leftLeg.setStrokeWidth(3.5);
        rightLeg.setStrokeWidth(3.5);
        leftLeg.setStrokeLineCap(StrokeLineCap.ROUND);
        rightLeg.setStrokeLineCap(StrokeLineCap.ROUND);
        Polyline zig = new Polyline(
                16.0, 18.0,
                20.0, 10.0,
                24.0, 26.0,
                28.0, 10.0,
                32.0, 26.0,
                36.0, 10.0,
                40.0, 26.0,
                44.0, 18.0
        );
        zig.setStrokeWidth(5);
        zig.setStrokeLineCap(StrokeLineCap.ROUND);
        zig.setStroke(new LinearGradient(
                0,0,1,0,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ffcc66")),
                new Stop(1, Color.web("#f59e0b"))
        ));
        zig.setFill(Color.TRANSPARENT);

        // highlight
        Polyline hi = new Polyline(
                16.0, 16.3,
                20.0, 9.0,
                24.0, 24.8,
                28.0, 9.0,
                32.0, 24.8,
                36.0, 9.0,
                40.0, 24.8,
                44.0, 16.3
        );
        hi.setStrokeWidth(2);
        hi.setStrokeLineCap(StrokeLineCap.ROUND);
        hi.setStroke(Color.rgb(255,255,255,0.35));
        hi.setFill(Color.TRANSPARENT);

        Pane p = new Pane(leftLeg, rightLeg, zig, hi);
        p.setPrefSize(60, 34);
        p.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.35)));
        return p;
    }

    private Node iconCapacitor3D() {
        Line leftLeg = new Line(6, 18, 18, 18);
        Line rightLeg = new Line(42, 18, 54, 18);
        leftLeg.setStroke(Color.web("#94a3b8"));
        rightLeg.setStroke(Color.web("#94a3b8"));
        leftLeg.setStrokeWidth(3.5);
        rightLeg.setStrokeWidth(3.5);
        leftLeg.setStrokeLineCap(StrokeLineCap.ROUND);
        rightLeg.setStrokeLineCap(StrokeLineCap.ROUND);
        Line plate1 = new Line(22, 8, 22, 28);
        Line plate2 = new Line(38, 8, 38, 28);
        plate1.setStrokeWidth(6);
        plate2.setStrokeWidth(6);
        plate1.setStrokeLineCap(StrokeLineCap.ROUND);
        plate2.setStrokeLineCap(StrokeLineCap.ROUND);
        plate1.setStroke(new LinearGradient(
                0,0,0,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#b7f7d1")),
                new Stop(1, Color.web("#22c55e"))
        ));
        plate2.setStroke(new LinearGradient(
                0,0,0,1,true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#b7f7d1")),
                new Stop(1, Color.web("#22c55e"))
        ));
        Line hi1 = new Line(21, 10, 21, 26);
        hi1.setStrokeWidth(2);
        hi1.setStroke(Color.rgb(255,255,255,0.35));
        Line hi2 = new Line(37, 10, 37, 26);
        hi2.setStrokeWidth(2);
        hi2.setStroke(Color.rgb(255,255,255,0.35));
        Pane p = new Pane(leftLeg, rightLeg, plate1, plate2, hi1, hi2);
        p.setPrefSize(60, 34);
        p.setEffect(new DropShadow(10, Color.rgb(0,0,0,0.35)));
        return p;
    }

    private void showToast(String msg, boolean danger) {
        toastLabel.setText(msg);
        toastLabel.setStyle(
                "-fx-background-color: " + (danger ? "rgba(239,68,68,0.92)" : "rgba(17,24,39,0.88)") + ";" +
                        "-fx-background-radius: 999;" +
                        "-fx-padding: 8 12;" +
                        "-fx-text-fill: white;"
        );

        FadeTransition ft = new FadeTransition(Duration.millis(180), toastLabel);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();

        FadeTransition out = new FadeTransition(Duration.millis(600), toastLabel);
        out.setDelay(Duration.seconds(1.6));
        out.setFromValue(1);
        out.setToValue(0);
        out.play();
    }
    private boolean isValidNumber(String s) {
        try {
            if (s == null) return false;
            String t = s.trim();
            if (t.isEmpty()) return false;
            Double.parseDouble(t);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    private void styleInput(TextField tf) {
        tf.setStyle(darkMode
                ? "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-background-color: #0f172a;" +
                "-fx-text-fill: #e5e7eb;" +
                "-fx-prompt-text-fill: rgba(229,231,235,0.45);" +
                "-fx-border-color: rgba(255,255,255,0.12);" +
                "-fx-padding: 10 12;"
                : "-fx-background-radius: 14;" +
                "-fx-border-radius: 14;" +
                "-fx-background-color: white;" +
                "-fx-text-fill: #111827;" +
                "-fx-border-color: rgba(0,0,0,0.10);" +
                "-fx-padding: 10 12;"
        );
    }
    private String cardStyle() {
        return darkMode
                ? "-fx-background-color: rgba(17,24,39,0.72);" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(255,255,255,0.10);" +
                "-fx-border-width: 1.2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.35), 14, 0.25, 0, 4);"
                : "-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: rgba(0,0,0,0.08);" +
                "-fx-border-width: 1.2;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 10, 0.25, 0, 3);";
    }
    private Button pillButton(String text) {
        Button b = new Button(text);
        b.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        b.setEffect(new DropShadow(6, Color.rgb(0,0,0,0.18)));

        if (darkMode) {
            b.setTextFill(Color.web("#e5e7eb"));
            b.setStyle(
                    "-fx-background-radius: 999;" +
                            "-fx-border-radius: 999;" +
                            "-fx-background-color: linear-gradient(#121c33,#0d1426);" +
                            "-fx-border-color: rgba(255,255,255,0.12);" +
                            "-fx-padding: 8 14;"
            );
        } else {
            b.setTextFill(Color.web("#111827"));
            b.setStyle(
                    "-fx-background-radius: 999;" +
                            "-fx-border-radius: 999;" +
                            "-fx-background-color: linear-gradient(#ffffff,#dfe6ee);" +
                            "-fx-border-color: rgba(0,0,0,0.10);" +
                            "-fx-padding: 8 14;"
            );
        }
        return b;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
