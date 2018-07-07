import lenz.htw.zpifub.Update;

import static java.lang.Math.abs;
import static java.lang.Math.atan;

public class Bot {
    public int _botNr;
    public float _move_x;
    public float _move_y;
    public Field _pos;
    public int _radius;
    public int _counter = 0;
    private int startAreaTurnX = 0;
    private int startAreaTurnY = 0;
    private double _away_angle=0.0;
    private Field _old_pos;
    private int _old_cycle=0;


    public Bot(int botNr, float move_x, float move_y) {
        _botNr = botNr;
        _move_x = move_x;
        _move_y = move_y;
    }

    private void log(String msg) {
        System.out.println(Board._updateNo+": Bot["+_botNr+"@"+_pos._x+","+_pos._y+"|"+_move_x+","+_move_y+"]"+msg);
    }

    public void updatePos(Update update, Board board, Client client) {
        if( update.type==null ) {
            if( update.player==client.getRemoteClient().getMyPlayerNumber() &&
                update.bot==_botNr) {
                _pos = board.getField(update.x, update.y);
            }
        }
        _radius=client.getRemoteClient().getInfluenceRadiusForBot(_botNr);
    }

    public void move(Board board, Client client, PowerUps powerUps) {
        _counter+=1;

        if( _pos == null ) {
            _pos = board.getBotPos(_botNr);
        }

        if(  didNotMove() ) {
            _move_x = _move_x*-1;
            _move_y = _move_y*-1;
        }
        else {
            moveToNearestPU(powerUps, board);
            paintArea(board);

            if ( //!moveToNearestPU(powerUps, board) &&
                //!paintArea(board) &&
                    collDetect(board, _move_x, _move_y)) {
                _move_x = _move_x * -1;
                _move_y = _move_y * -1;
            }
        }
        client.getRemoteClient().setMoveDirection(_botNr, _move_x, _move_y);
    }

    private boolean didNotMove() {
        if( _pos==null || _old_pos==null)
            return false;

        if( abs(_old_pos._x - _pos._x)<=16 ||
            abs(_old_pos._y - _pos._y)<=16 ) {
            log("didNotMove() > detected");
            return true;
        }

        _old_pos = _pos;
        _old_cycle = Board._updateNo;
        return false;
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

    private boolean collDetect(Board board, float intentedX, float intendedY){
        if( _pos == null )
            return false;

        Field nextField = null;
        int steps=17;//_radius+1;
        for( int i=0; i<steps; ++i) {
            nextField=calcNextField(board,intentedX, intendedY, i);
            if( !nextField._isWalkable || nextField._tempBlock ) {
                log("::collDetect() @"+nextField.toString());
                _away_angle = awayAngleFromMe(nextField);
//                // LOG SURROUNDING FIELDS of collision
//                for(int fooX=-1; fooX<2; ++fooX) {
//                    for( int fooY=-1; fooY<2; ++fooY) {
//                        int nbX = nextField._x + fooX;
//                        int nbY = nextField._y + fooY;
//                        if( nbX>1023 ) nbX=1023;
//                        if( nbX<0 ) nbX=0;
//                        if( nbY>1023 ) nbY=1023;
//                        if( nbY<0 ) nbY=0;
//                        log("::collDetect()      NB FIELDS:"+board._board[nbX][nbY].toString());
//                    }
//                }

//                // LOG SURROUNDING FIELDS
//                for(int fooX=-1; fooX<2; ++fooX) {
//                    for( int fooY=-1; fooY<2; ++fooY) {
//                        int nbX = _pos._x + fooX*17;
//                        int nbY = _pos._y + fooY*17;
//                        if( nbX>1023 ) nbX=1023;
//                        if( nbX<0 ) nbX=0;
//                        if( nbY>1023 ) nbY=1023;
//                        if( nbY<0 ) nbY=0;
//                        log("::collDetect() MY NB FIELDS:"+board._board[nbX][nbY].toString());
//                    }
//                }
                return true;
            }
        }

//        // check surrounding fields
//        for(int fooX=-1; fooX<2; ++fooX) {
//            for( int fooY=-1; fooY<2; ++fooY) {
//                int nbX = _pos._x + fooX*17;
//                int nbY = _pos._y + fooY*17;
//                if( nbX>1023 ) nbX=1023;
//                if( nbX<0 ) nbX=0;
//                if( nbY>1023 ) nbY=1023;
//                if( nbY<0 ) nbY=0;
//                Field field=board._board[nbX][nbY];
//                if( !field._isWalkable || field._tempBlock ) {
//                    log("::collDetect() COLL on NB FIELD@" + nextField.toString());
//                    return true;
//                }
//            }
//        }

        if( (_pos._x+steps>1023 && _move_x>0) ||
            (_pos._x-steps<0 && _move_x<0) ||
            (_pos._y+steps>1023 && _move_y>0) ||
            (_pos._y-steps<0 && _move_y<0) ) {
            log("::collDetect() -> BORDERFIELD");
            return true;
        }

        return false;
    }

    private Field calcNextField(Board board, float intentedX, float intendedY, int step){
        float newXPosf = _pos._x + intentedX*step;
        int newXPos = Math.round(newXPosf);
        if(newXPos > 1023) newXPos = 1023;
        if(newXPos < 0) newXPos = 0;
        float newYPosf = _pos._y + intendedY*step;
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
        double angle = 45;

        if( collDetect(board,_move_x,_move_y) ) {
            float[] moveVector = calcNewDirVector(angle);
            move_x = _move_x + (moveVector[0]) % 1;
            move_y = _move_x + (moveVector[1]) % 1;
            log("::paintArea() -> COLL Turn!");
            if( collDetect(board,move_x,move_y)) {
                log("::paintArea() -> COLL Turn on NEW direction -> turn away");
                moveVector = calcNewDirVector(_away_angle);
                move_x = _move_x + (moveVector[0]) % 1;
                move_y = _move_x + (moveVector[1]) % 1;
            }
            startAreaTurnX = _pos._x;
            startAreaTurnY = _pos._y;
        }
        else
        if( (startAreaTurnY != 0.0f || startAreaTurnX != 0.0f ) &&
            abs(startAreaTurnX-_pos._x) +abs(startAreaTurnY-_pos._y)>2*_radius ) {
            log("::paintArea() -> 2x bot._radius drawn - Turn!");
            float[] moveVector = calcNewDirVector(angle);
            move_x = _move_x + (moveVector[0]) % 1;
            move_y = _move_x + (moveVector[1]) % 1;
            if( collDetect(board,move_x,move_y)) {
                log("::paintArea() -> 2x bot._radius drawn - Turn! Coll on new dir! -> turnaround");
                angle *= -1;
                moveVector = calcNewDirVector(angle);
                move_x = (moveVector[0]) % 1;
                move_y = (moveVector[1]) % 1;
            }
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
        double x = _pos._x;
        double y = _pos._y;

        double radians = angle;// Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double xNew = (x * cos) - (y * sin);
        double yNew = (x * sin) + (y * cos);
        vector[0] = (float) xNew;
        vector[1] = (float) yNew;

        return vector;
    }

    private double awayAngleFromMe(Field field) {
        double angle = 0.0;
        double a = abs( _pos._y - field._y);
        double b = abs( _pos._x - field._x);
        angle = Math.atan( b/a );
        return angle;
    }
}
