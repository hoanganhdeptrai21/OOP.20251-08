package OOPLab20251.Component;

public class Destination extends Component {

    public Destination(String name) {
        super(name);
        this.setVoltage(0.0);
    }

    @Override
    public void calculateAttributes() {
        setVoltage(0.0);
        System.out.println("OOPLab20251.Component.Destination " + getName() + " grounded at 0V");
    }
}