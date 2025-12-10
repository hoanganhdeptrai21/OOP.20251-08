public class Wire extends Component{

    public Wire(String name){
        super(name);
    }

    @Override
    public void calculateAttributes() {
        setCurrent(0.0);
    }
}
