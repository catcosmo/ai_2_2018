import lenz.htw.zpifub.Update;

import java.util.List;

import static java.lang.Float.isNaN;
import static java.lang.Math.abs;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

public class Bot {
    private static final boolean _use_dijkstra = true;
    public static final int RASTER_SIZE_HOTAREA=128;
    public static final int RASTER_SIZE_DIJKSTRA=16;

    public int _botNr;
    public float _move_x;
    public float _move_y;
    public Field _pos;
    public int _radius;
    public int _counter = 0;
    public boolean _turnsRight = true;
    private int _startAreaTurnX = 0;
    private int _startAreaTurnY = 0;
    private double _away_angle=0.0;
    private Field _old_pos;
    private int _old_cycle=0;
    private boolean _did_not_move=false;
    private RasterNode _hottest_area=null;
    private List<RasterNode> _my_way = null;

    public Bot(int botNr, float move_x, float move_y) {
        _botNr = botNr;
        _move_x = move_x;
        _move_y = move_y;
    }

    private void log(String msg) {
        int rId = Board.getRasterID(_pos._x,_pos._y, RASTER_SIZE_HOTAREA);
        System.out.println(Board._updateNo+": Bot["+_botNr+"@<"+rId+">"+_pos._x+","+_pos._y+"|"+_move_x+","+_move_y+"]"+msg);
    }

    public void updatePos(Update update, Board board, Client client) {
        if( update.type==null ) {
            if( update.player==client.getRemoteClient().getMyPlayerNumber() &&
                update.bot==_botNr) {
                _pos = board.getField(update.x, update.y);
                if( _old_pos==null) {
//                    _old_pos=_pos;
//                    _old_cycle = Board._updateNo;
                }
                else {
                    //checkDidNotMove();
                }
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
            _move_x = _move_x*-0.7f;
            _move_y = _move_y*-1;
            moveToHottestArea(board);
        }
        else
            {
            // give it a direction
            if( !moveToNearestPU(powerUps, board)) {
                moveToHottestArea(board);
            }

            // turn on next collision
            // whatever way we are
            // and paint area
            // paintArea(board);

                // NOT NEEDED ANYMORE?
//            // safety collision check
//            if (collDetect(board, _move_x, _move_y)) {
//                _move_x = _move_x * -1;
//                _move_y = _move_y * -1;
//            }
        }
        if( isNaN(_move_x) ) {
            _move_x = -1;
        }
        if( isNaN(_move_y) ) {
            _move_y = -1;
        }
        client.getRemoteClient().setMoveDirection(_botNr, _move_x, _move_y);
    }

    private void checkDidNotMove() {
        if( _did_not_move ) {
            _did_not_move = false;
            // log("::checkDidNotMove() reset flag");
            return;
        }
        int diffX = abs(_old_pos._x - _pos._x);
        int diffY = abs(_old_pos._y - _pos._y);
        if( diffX > 0 || diffY > 0 ) {
            double moved = sqrt(diffX^2 + diffY^2);
            //if( diffX <= 10 && diffY <= 10 ){
            if( moved < 2.3 && diffX<4 && diffY<4 ) {
                _did_not_move = true;
                log("::checkDidNotMove() didNotMove NOK >"+_old_cycle+"< old:"+_old_pos._x+","+_old_pos._y+" cur:"+_pos._x+","+_pos._y + "-> "+diffX+","+diffY+" moved:"+moved);
            }
            else {
                //log("::checkDidNotMove() didNotMove OK >"+_old_cycle+"< old:"+_old_pos._x+","+_old_pos._y+" cur:"+_pos._x+","+_pos._y + "-> "+diffX+","+diffY+" moved:"+moved);
            }
        } else {
            log("::checkDidNotMove() didNotMove NOK SAME >"+_old_cycle+"< old:"+_old_pos._x+","+_old_pos._y+" cur:"+_pos._x+","+_pos._y + "-> "+diffX+","+diffY);
            _did_not_move = true;
        }
    }

    private boolean didNotMove() {
        if( _pos==null )
            return false;

        if( _old_pos==null ) {
            _old_pos = _pos;
            _old_cycle = Board._updateNo;
            return false;
        }

        checkDidNotMove();
        if( _did_not_move ) {
            return true;
        }

        // UPDATE OLD POS HERE
        // so that checkDidNotMove()
        // can check for same Pos!!!
        _old_pos = _pos;
        _old_cycle = Board._updateNo;
        return false;
    }

    public boolean moveToNearestPU(PowerUps powerUps, Board board) {
        if( _pos == null )
            return false;

        float move_x = 0.0f;
        float move_y = 0.0f;

        PowerUp nearestPU = powerUps.findNearest(this);
        if(nearestPU != null) {
//            if (!isPathClear(nearestPU._x, nearestPU._y, board)) {
//                log("::moveToNearestPU() can not find direct path!!!");
//                return false;
//            }
            float[] dir = getDirection(nearestPU._x, nearestPU._y);
            move_x = dir[0];
            move_y = dir[1];
        }
        if( move_x!=0.0f || move_y!=0.0f) {
            _move_x = move_x;
            _move_y = move_y;
            return true;
        }
        return false;
    }

    public boolean moveToHottestArea(Board board) {
        if( _pos == null )
            return false;
        float move_x = 0.0f;
        float move_y = 0.0f;

        int bots_area_id = Board.getRasterID(_pos._x, _pos._y, Bot.RASTER_SIZE_HOTAREA) ;
        if( _hottest_area!=null && (bots_area_id==_hottest_area.get_numberID() || _did_not_move) ) {
            log("Hottest Area REACHED:" + _hottest_area.get_numberID() + " @" + _hottest_area.get_middleX() + "," +_hottest_area.get_middleY());
            if( _did_not_move ) {
                //board.ignoreRasterId(_hottest_area.get_numberID());
            }
            _hottest_area=null;
            return false;
        }
        else
        if( _hottest_area!=null ) {
            if( _use_dijkstra && _my_way!=null && _my_way.size()>0 ) {
                RasterNode firstNode = _my_way.get(0);
                int x = firstNode.get_middleX();
                int y = firstNode.get_middleY();
                _my_way.remove(0);

                float[] dir = getDirection(x, y);
                move_x = dir[0];
                move_y = dir[1];

                if (move_x != 0.0f || move_y != 0.0f) {
                    _move_x = move_x;
                    _move_y = move_y;
                    return true;
                }

            }
            return false;
        }

        //get raster
        //check if fast bot, if so: prioritize white areas
        boolean fastBot = _radius==40;
        //SIZE AND WEIGHT OF HOT AREA RASTER DEFINED HERE!
        RasterNode[] rasterNodes = board.getRaster(RASTER_SIZE_HOTAREA, 9, 50000, fastBot, false);

        //get hottest area
        RasterNode rasterNode = board.getHotArea(rasterNodes, this, false, !_use_dijkstra);
        //get x,y coordinates of center = goal
        int fieldCenterX = rasterNode.get_startX()+rasterNode.get_size()/2;
        int fieldCenterY = rasterNode.get_startY()+rasterNode.get_size()/2;
        if( _hottest_area!=null && _hottest_area.get_numberID()==rasterNode.get_numberID() ) {

        } else {
            log("Hottest Area is:" + rasterNode.get_numberID() + " @" + fieldCenterX + "," +fieldCenterY);
        }

        // DIRECT MOVE to target
        if( !_use_dijkstra ) {

        }
        else
        if( fastBot ) {
            //Djikstra move to target
            RasterNode[] all_nodes = board.getRaster(RASTER_SIZE_DIJKSTRA, 3, 10000, fastBot, false);

            int me = board.getRasterID(_pos._x, _pos._y, RASTER_SIZE_DIJKSTRA);
            RasterNode my_node = all_nodes[me];
            Graph.calculateShortestPathFromSource(all_nodes, my_node);
            int tar = board.getRasterID(fieldCenterX, fieldCenterY, RASTER_SIZE_DIJKSTRA);
            RasterNode target = all_nodes[tar];
            _my_way = target.get_shortestPath();
            if (_my_way.size() <= 1) {
                log(" djisktra empty!");
            } else {
                log(" djisktra OK! to:"+ fieldCenterX+","+ fieldCenterY+" waypts:" + _my_way.size());
                RasterNode firstNode = _my_way.get(1);
                fieldCenterX = firstNode.get_middleX();
                fieldCenterY = firstNode.get_middleY();
                //board.saveBoard(_my_way);
                _my_way.remove(0);
                _my_way.remove(1);
            }
        }

//        if (!isPathClear(fieldCenterX, fieldCenterY, board)) {
//            return false;
//        }

        float[] dir = getDirection(fieldCenterX, fieldCenterY);
        move_x = dir[0];
        move_y = dir[1];

        if (move_x != 0.0f || move_y != 0.0f) {
            _move_x = move_x;
            _move_y = move_y;
            _hottest_area = rasterNode;
            return true;
        }

        return false;
    }

    // calculate movement vector to GOAL
    private float[] getDirection(int goalX, int goalY) {
        float[] ret = new float[2];
        int diffX = (_pos._x - goalX) * -1;
        int diffY = (_pos._y - goalY) * -1;
        float dist = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
        ret[0] = (float) (diffX * (1.0f / dist));
        ret[1] = (float) (diffY * (1.0f / dist));
        while( ret[0]>1.0f ) {
            ret[0] = -1 + (ret[0]-1.0f);
        }
        while( ret[0]<-1.0f ) {
            ret[0] = 1 + (ret[0]+1.0f);
        }

        while ( ret[1]>1.0f ) {
            ret[1] = -1 + (ret[1]-1.0f);
        }
        while ( ret[1]<-1.0f ) {
            ret[1] = 1 + (ret[1]+1.0f);
        }
        return ret;
    }

    protected boolean isPathClear(int goalX, int goalY, Board board) {
        float[] dir = getDirection(goalX, goalY);
        float move_x = dir[0];
        float move_y = dir[1];
        long steps = 0;
        int a2= abs(_pos._x-goalX);
        a2 *= a2;
        int b2= abs(_pos._y-goalY);
        b2 *= b2;
        steps = round(sqrt(a2+b2));
        if( collDetect(board,move_x,move_y, steps) ) {
            //log("::isPathClear() NOK to:"+goalX +"," + goalY + " with steps:" +steps);
            return false;
        }
        //log("::isPathClear() OK to:"+goalX +"," + goalY + " with steps:" +steps);
        return true;
    }

    private boolean collDetect(Board board, float intentedX, float intendedY){
        int steps=17;//_radius+1;
        return collDetect(board, intentedX, intendedY, steps);
    }
    private boolean collDetect(Board board, float intendedX, float intendedY, long steps){
        if( _pos == null )
            return false;

        Field nextField = null;
        for( int i=0; i<steps; ++i) {
            nextField=calcNextField(board,intendedX, intendedY, i);
            if( !nextField._isWalkable || nextField._tempBlock ) {
                // log("::collDetect("+intendedX+","+intendedY+") COLL on step:"+i+" on next FIELD@"+nextField.toString());
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

//            // check surrounding fields
//            for(int fooX=-1; fooX<2; ++fooX) {
//                for( int fooY=-1; fooY<2; ++fooY) {
//                    int nbX = nextField._x + fooX*17;
//                    int nbY = nextField._y + fooY*17;
//                    if( nbX>1023 ) nbX=1023;
//                    if( nbX<0 ) nbX=0;
//                    if( nbY>1023 ) nbY=1023;
//                    if( nbY<0 ) nbY=0;
//                    Field field=board._board[nbX][nbY];
//                    if( !field._isWalkable || field._tempBlock ) {
//                        log("::collDetect("+intendedX+","+intendedY+") COLL on step:"+i+" on  next NB FIELD@" + field.toString());
//                        return true;
//                    }
//                }
//            }
        }

//        if( (_pos._x+steps>1023 && _move_x>0) ||
//            (_pos._x-steps<0 && _move_x<0) ||
//            (_pos._y+steps>1023 && _move_y>0) ||
//            (_pos._y-steps<0 && _move_y<0) ) {
//            log("::collDetect() -> BORDERFIELD");
//            return true;
//        }

        return false;
    }

    private Field calcNextField(Board board, float intentedX, float intendedY, int step){
        float newXPosf = _pos._x + intentedX*step;
        int newXPos = round(newXPosf);
        if(newXPos > 1023) newXPos = 1023;
        if(newXPos < 0) newXPos = 0;
        float newYPosf = _pos._y + intendedY*step;
        int newYPos = round(newYPosf);
        if(newYPos > 1023) newYPos = 1023;
        if(newYPos < 0) newYPos = 0;
        return board._board[newXPos][newYPos];
    }


    private boolean paintArea(Board board){
        if( _pos == null )
            return false;

        float move_x = 0.0f;
        float move_y = 0.0f;
        int angle = 90;
        //calculate distance since turn
        long distance = 0;
        int a2= abs(_pos._x-_startAreaTurnX);
        a2 *= a2;
        int b2= abs(_pos._y-_startAreaTurnX);
        b2 *= b2;
        distance = round(sqrt(a2+b2));

        if(!_turnsRight)
            angle*=-1;


        //walk in one direction
        //until you hit obstacle
        if (collDetect(board, _move_x, _move_y)) {
            float[] moveVector = calcNewDirVector(angle);
            //turn 90° right
            move_x = moveVector[0];
            move_y = moveVector[1];
            _startAreaTurnX = _pos._x;
            _startAreaTurnY = _pos._y;
            log("paintArea() COLL - first turn");
         }
        //walk 2 * your own radius
        //turn 90° right
         else if(_startAreaTurnY != 0 && _startAreaTurnX != 0 && distance > _radius*3){
            float[] moveVector = calcNewDirVector(angle);
            //turn 90° right
            move_x = moveVector[0];
            move_y = moveVector[1];
            _startAreaTurnX = 0;
            _startAreaTurnY = 0;
            //repeat with 90° left
            _turnsRight = !_turnsRight;
            log("paintArea() 2x radius drawn - turn");
        }


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
        double x = _move_x;
        double y = _move_y;

        double radians = Math.toRadians(angle);
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
        if(a == 0)
            a = 0.1;
        angle = Math.atan( b/a );
        return angle;
    }
}
