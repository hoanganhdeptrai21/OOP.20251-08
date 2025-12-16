package OOPLab20251.Component;

public class Source extends Component {

    public Source(String name, double supplyVoltage) {
        super(name);
        this.setVoltage(supplyVoltage);
    }
}