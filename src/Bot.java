import lenz.htw.zpifub.Update;

public class Bot {
    public int _botNr;
    public float _move_x;
    public float _move_y;
    public Field _pos;
    public int _counter = 0;
    private int startAreaTurnX = 0;
    private int startAreaTurnY = 0;


    public Bot(int botNr, float move_x, float move_y) {
        _botNr = botNr;
        _move_x = move_x;
        _move_y = move_y;
    }

    public void updatePos(Update update, Board board, Client client) {
        if( update.type==null ) {
            if( update.player==client.getRemoteClient().getMyPlayerNumber() &&
                update.bot==_botNr) {
                _pos = board.getField(update.x, update.y);
            }
        }
    }

    public void move(Board board, Client client, PowerUps powerUps) {
        _counter+=1;

        if( _pos == null ) {
            _pos = board.getBotPos(_botNr);
        }

        moveToNearestPU(powerUps, board);

        if( //!moveToNearestPU(powerUps, board) &&
            //!paintArea(board) &&
            collDetect(board) ) {
            _move_x = _move_x*-1;
            _move_y = _move_y*-1;
        }
        client.getRemoteClient().setMoveDirection(_botNr, _move_x, _move_y);
    }

    public boolean moveToNearestPU(PowerUps powerUps, Board board) {
        if( _pos == null )
            return false;

        float move_x = 0.0f;
        float move_y = 0.0f;

        PowerUp nearestPU = powerUps.findNearest(_pos._x, _pos._y);
        if(nearestPU != null) {
            int diffX = (_pos._x - nearestPU._x) * -1;
            int diffY = (_pos._y - nearestPU._y) * -1;
            float dist = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
            move_x = (float) (diffX * (1.0 / dist));
            move_y = (float) (diffY * (1.0 / dist));

            // TODO schnellster bot am PU soll fahren die anderen nicht
            if (!isPathClear(move_x, move_y, nearestPU, board)) {
                return false;
            }
        }
        if( move_x!=0.0f || move_y!=0.0f) {
            _move_x = move_x;
            _move_y = move_y;
            return true;
        }
        return false;
    }

    private boolean isPathClear(float move_x, float move_y, PowerUp nearestPU, Board board) {
        //starting position
        Field temp = _pos;
        Field next = null;
        float newXPosf;
        float newYPosf;
        int newXPos;
        int newYPos;
        float tempCarryX = 0.0f; //Uebertrag von Runden
        float tempCarryY = 0.0f;
        // check all fields on the way until you reach destination (=PU)
        while (temp._isWalkable && temp._x!=nearestPU._x && temp._y != nearestPU._y){
            // calc next Field
            newXPosf = temp._x + move_x + tempCarryX;
            if( tempCarryX!=0.0f ) {
                tempCarryX=0.0f;
            }
            newXPos = Math.round(newXPosf);
            if (newXPos == temp._x){
                tempCarryX = move_x;
            }
            newYPosf = temp._y + move_y + tempCarryY;
            if( tempCarryY!=0.0f ) {
                tempCarryY=0.0f;
            }
            newYPos = Math.round(newYPosf);
            if (newYPos == temp._y){
                tempCarryY = move_y;
            }
            //check if field is walkable
            next = board._board[newXPos][newYPos];
            if(next._isWalkable){
                temp = next;
            } else
                return false;
        }
        return true;
    }

    private boolean collDetect(Board board){
        if( _pos == null )
            return false;

        Field nextField = calcNextField(board);
        return (!nextField._isWalkable || nextField._tempBlock);
    }

    private Field calcNextField(Board board){
        float newXPosf = _pos._x + _move_x*20;
        int newXPos = Math.round(newXPosf);
        if(newXPos > 1023) newXPos = 1023;
        if(newXPos < 0) newXPos = 0;
        float newYPosf = _pos._y + _move_y*20;
        int newYPos = Math.round(newYPosf);
        if(newYPos > 1023) newYPos = 1023;
        if(newYPos < 0) newYPos = 0;
        return board._board[newXPos][newYPos];
    }


    private boolean paintArea(Board board) {
        if( _pos == null )
            return false;

        float move_x = 0.0f;
        float move_y = 0.0f;

        if (collDetect(board)) {
            float[] moveVector = calcNewDirVector(180);
            move_x = moveVector[0];
            move_y = moveVector[1];
            startAreaTurnX = _pos._x;
            startAreaTurnY = _pos._y;
        } else if (startAreaTurnY != 0 || startAreaTurnX != 0 || (startAreaTurnX-_pos._x)+(startAreaTurnY-_pos._y)>40){
            float[] moveVector = calcNewDirVector(180);
            move_x = moveVector[0];
            move_y = moveVector[1];
            startAreaTurnX = 0;
            startAreaTurnY = 0;
        }

        // startest? -> merke startcoord als member und laufe rechts
        // läuft schon -> wo bin ich? weit genug nach rechts -> runter sonst weiter
        //


        if( move_x!=0.0f || move_y!=0.0f) {
            _move_x=move_x;
            _move_y=move_y;
            return true;
        }
        return false;
    }

    //calculate new x/y coordinates for movement direction, input angle in which to change
    private float[] calcNewDirVector(double angle){
        float[] vector = new float[2];
        int x = _pos._x;
        int y = _pos._y;

        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double xNew = (x * cos) - (y * sin);
        double yNew = (x * sin) + (y * cos);
        vector[0] = (float) xNew;
        vector[1] = (float) yNew;

        return vector;
    }
}
