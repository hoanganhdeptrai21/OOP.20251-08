package OOPLab20251.Component;

public class Capacitor extends Component {
    private double capacitance;
    private double prevVoltage;
    private static final double DELTA_TIME = 0.016; // 60 FPS ~ 0.016s

    public Capacitor(String name, double capacitance) {
        super(name);
        this.capacitance = capacitance;
        this.prevVoltage = 0.0;
    }

    @Override
    public void calculateAttributes() {
        double currentVoltage = getVoltage();

        // ✅ thay cho dòng "..."
        double dV = currentVoltage - prevVoltage;
        double dT = DELTA_TIME;

        double current = capacitance * dV / dT;
        setCurrent(current);

        prevVoltage = currentVoltage;
    }

    public void setCapacitance(double capacitance) {
        this.capacitance = capacitance;
    }

    public double getCapacitance() {
        return capacitance;
    }
}
