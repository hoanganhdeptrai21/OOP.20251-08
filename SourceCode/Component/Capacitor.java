package OOPLab20251.Component;

public class Capacitor extends Component{
    private double capacitance;
    private double prevVoltage;
    private static final double DELTA_TIME = 0.016;

    public Capacitor(String name, double capacitance){
        super(name);
        this.capacitance = capacitance;
        this.prevVoltage = 0.0;
    }

    @Override
    public void calculateAttributes() {
        double currentVoltage = getVoltage();
        double dV = currentVoltage - prevVoltage;
        double dT = DELTA_TIME;
        double current = capacitance * dV/dT;
        setCurrent(current);

        prevVoltage = currentVoltage;
    }

    public double getCapacitance() {
        return capacitance;
    }
}
