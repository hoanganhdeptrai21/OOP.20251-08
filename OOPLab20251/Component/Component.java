package OOPLab20251.Component;

public abstract class Component {
    private String name;
    private double voltage;
    private double current;
    private boolean isLocked = false;
    private boolean canRotate = true;
    private int rotationDegree = 0;

    public int rotate(){
        this.rotationDegree = (rotationDegree + 90) % 360;
        return this.rotationDegree;
    }

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
    public boolean canRotate() {
        return canRotate;
    }
    public void setCanRotate(boolean canRotate) {
        this.canRotate = canRotate;
    }
    public int getRotationDegree() {return rotationDegree;}
    public abstract void calculateAttributes();
    public String toString() {
        return "Name: " + name;
    }
    
}