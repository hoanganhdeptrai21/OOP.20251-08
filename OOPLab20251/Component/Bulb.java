package OOPLab20251.Component;

public class Bulb extends Component {
    private boolean isLit;

    public Bulb(String name) {
        super(name);
        this.isLit = false;
    }

    public boolean isLit() {
        return isLit;
    }

    public void setLit(boolean lit) {
        this.isLit = lit;
    }

    @Override
    public void calculateAttributes() {
        double threshold = 0.0001;

        if (Math.abs(getCurrent()) > threshold) {
            this.isLit = true;
            System.out.println(getName() + " is ON (Current: " + getCurrent() + ")");
        } else {
            this.isLit = false;
            System.out.println(getName() + " is OFF");
        }
    }
}