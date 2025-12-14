package OOPLab20251.Component;

public class Source extends Component {

    public Source(String name, double supplyVoltage) {
        super(name);
        this.setVoltage(supplyVoltage);
    }

    @Override
    public void calculateAttributes() {
        System.out.println("OOPLab20251.Component.Source " + getName() + " providing " + getVoltage() + "V");
    }
}