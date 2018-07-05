public class Board {
    Field[][] _board = new Field[1024][1024];
    Client _client = null;

    public Board(Client client) {
        _client=client;
    }

    public Board initBoard(){
        for (int y = 0; y < 1024; y++) {
            for (int x = 0; x < 1024; x++) {
                Field field = new Field();
                field._puType = null;
                field._color = _client.getRemoteClient().getBoard(x,y);
                field._isWalkable = _client.getRemoteClient().isWalkable(x,y);

                _board[x][y] = field;
            }
        }
    }
}
