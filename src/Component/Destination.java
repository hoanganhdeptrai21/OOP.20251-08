package src.Component;

public class Destination extends Component {

    public Destination(String name) {
        super(name);
        this.setVoltage(0.0);
    }

    public void calculateAttributes() {
        setVoltage(0.0);
        System.out.println("Destination " + getName() + " grounded at 0V");
    }
}