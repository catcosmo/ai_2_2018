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

    public void move(Board board, Client client) {
        _pos = board.getBotPos(_botNr);
        // _move_x =
        // _move_y =
        client.getRemoteClient().setMoveDirection(_botNr, _move_x, _move_y);
    }

    //private collision detection
    //private random moves
}
