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
    
}