package src.Component;

public class Block extends Component {
    public Block(String name){
        super(name);
        setLocked();
        setCanRotate(false);
    }
    public void calculateAttributes(){
        setCurrent(0.0);
    }
}
