import lenz.htw.zpifub.PowerupType;

public class Field implements Cloneable {
    public int _x;
    public int _y;
    public int _color;
    public boolean _isWalkable;
    public int _player = -1;
    public int _bot = -1;
    public PowerupType _puType = null;
    public boolean _hasPU = false;


    public Field() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
