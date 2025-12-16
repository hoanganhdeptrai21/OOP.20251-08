package OOPLab20251.Component;

public class Resistor extends Component {
    private double resistance; //R

    public Resistor(String name, double resistance) {
        super(name);
        this.resistance = resistance;
    }

    public double getResistance() {
        return resistance;
    }

    @Override
    public void calculateAttributes() {
        if (resistance > 0){
            double calculatedCurrent = getVoltage()/resistance;
            setCurrent(calculatedCurrent);
        }
    }
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

}
