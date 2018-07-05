import lenz.htw.zpifub.Update;

public class Bot {
    public int _botNr;
    public float _move_x;
    public float _move_y;
    public Field _pos;

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

    public void move(Board board, Client client) {
        if( _pos == null ) {
            _pos = board.getBotPos(_botNr);
        }
        if( _pos != null && collDetect(board) ) {
            _move_x = _move_x*-1;
            _move_y = _move_y*-1;
        }
        client.getRemoteClient().setMoveDirection(_botNr, _move_x, _move_y);
    }

    private boolean collDetect(Board board){
        Field nextField = calcNextField(board);
        return !nextField._isWalkable;
    }

    private Field calcNextField(Board board){
        float newXPosf = _pos._x + _move_x*20;
        int newXPos = Math.round(newXPosf);
        float newYPosf = _pos._y + _move_y*20;
        int newYPos = Math.round(newYPosf);
        return board._board[newXPos][newYPos];
    }
    //private random moves
}
