package src.Component;

public class Source extends Component {

    public Source(String name, double supplyVoltage) {
        super(name);
        this.setVoltage(supplyVoltage);
    }

    public void calculateAttributes() {
        System.out.println("Source " + getName() + " providing " + getVoltage() + "V");
    }
}