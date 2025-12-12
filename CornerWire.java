public class CornerWire extends Component {
    public CornerWire(String name) {
        super(name);
    }

    @Override
    public void calculateAttributes() {
        // Wires generally just pass current through, logic handled by board
    }
}