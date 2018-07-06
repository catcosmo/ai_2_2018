import lenz.htw.zpifub.PowerupType;

public class Field implements Cloneable {
    public static final int BLACK=0x00000000;
    public static final int WHITE=0xFFFFFFFF;
    public static final int PLAYER_0=0x00FF0000;
    public static final int PLAYER_1=0x0000FF00;
    public static final int PLAYER_2=0x000000FF;

    public int _x;
    public int _y;
    public int _color;
    public boolean _isWalkable;
    public int _player = -1;
    public int _bot = -1;
    public PowerupType _puType = null;
    public boolean _hasPU = false;
    public boolean _tempBlock = false;


    public Field() {
    }

    public void setColorFromPlayer(int player) {
        switch ( player ) {
            case 0:
                _color=Field.PLAYER_0;
                break;
            case 1:
                _color=Field.PLAYER_1;
                break;
            case 2:
                _color=Field.PLAYER_2;
                break;
        }
    }
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
