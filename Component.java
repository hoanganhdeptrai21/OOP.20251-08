public abstract class Component {
    private String name;
    private double voltage; //V
    private double current; //I
    private boolean isLocked = false;

    public boolean isLocked(){
        return isLocked;
    }
    public void setLocked(){
        this.isLocked = true;
    }
    public Component(String name) {
        this.name = name;
        this.voltage = 0.0;
        this.current = 0.0;
    }
    public String getName() {
        return name;
    }
    public double getVoltage() {
        return voltage;
    }
    public double getCurrent() {
        return current;
    }
    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }
    public void setCurrent(double current) {
        this.current = current;
    }
    public abstract void calculateAttributes();
    public String toString() {
        return "Name: " + name;
    }
}
