package OOPLab20251.Component;

public enum ComponentType{
    WIRE(Wire.class),
    CORNER_WIRE(CornerWire.class),
    RESISTOR(Resistor.class),
    CAPACITOR(Capacitor.class),

    SOURCE(Source.class),
    DESTINATION(Destination.class),
    BULB(Bulb.class),
    BLOCK(Block.class);

    private Class<?>  clazz;
    ComponentType(Class<?> clazz) {
        this.clazz = clazz;
    }
    public boolean isInstance(Component component) {
        return clazz.isInstance(component);
    }
}