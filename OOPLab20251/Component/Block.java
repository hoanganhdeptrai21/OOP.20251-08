package OOPLab20251.Component;

public class Block extends Component {
    public Block(String name){
        super(name);
        setLocked();
    }
    public void calculateAttributes(){
        setCurrent(0.0);
    }
}
