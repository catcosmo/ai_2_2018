import lenz.htw.zpifub.Update;

import static lenz.htw.zpifub.PowerupType.RAIN;

public class Board implements Cloneable {
    Field[][] _board = new Field[1024][1024];
    Client _client = null;
    DrawPanel _draw = new DrawPanel();
    int _updateNo = 0;

    public Board(Client client) {
        _client=client;
    }

    public void initBoard(){
        for (int y = 0; y < 1024; y++) {
            for (int x = 0; x < 1024; x++) {
                Field field = new Field();
                field._puType = null;
                field._color = _client.getRemoteClient().getBoard(x,y);
                field._isWalkable = _client.getRemoteClient().isWalkable(x,y);
                field._x = x;
                field._y = y;
                _board[x][y] = field;
            }
        }
        _draw.save(this,"pl_" + _client.getRemoteClient().getMyPlayerNumber() + "_init.png");
    }

    public void updateBoard(Update update){
        //check for PowerUp & place PowerUp
        if(update.player == -1 && update.bot == -1){
            _board[update.x][update.y]._puType = update.type;
            _board[update.x][update.y]._hasPU = true;
            saveBoard("PU");
            // if PU is NOT RAIN (so should be avoided)
//            // make 20px non walkable barrier around it so that the bots don't hit it
//            if(_board[update.x][update.y]._puType!=RAIN){
//                for (int x = update.x-20; x <= update.x+20 ; x++) {
//                    for (int y = update.y-20; y <= update.y+20; y++) {
//                        _board[x][y]._isWalkable = false;
//                    }
//                }
//            }
        }else if (update.player > -1 && update.bot > -1){
            // place player
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y]._bot = update.bot;
        } else if (update.player > -1 && update.bot == -1){
            // delete PowerUp and update player position
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y]._puType = null;
            _board[update.x][update.y]._hasPU = false;
            // clean up PU barrier
//            if(_board[update.x][update.y]._isWalkable == false){
//                for (int x = update.x-20; x <= update.x+20 ; x++) {
//                    for (int y = update.y - 20; y <= update.y + 20; y++) {
//                        _board[x][y]._isWalkable = true;
//                    }
//                }
//            }
        }
        _updateNo += 1;
    }

    public Field getBotPos(int botNr) {
        for (int y = 0; y < 1024; y++) {
            for (int x = 0; x < 1024; x++) {
                if( _board[x][y]._player == _client.getRemoteClient().getMyPlayerNumber() && _board[x][y]._bot == botNr)
                    return _board[x][y];
            }
        }
        return null;
    }

    public Field getField(int x, int y) {
        return _board[x][y];
    }

    public void saveBoard() {
        //if( _updateNo%10==0  ) {
            _draw.save(this,"u_" + _updateNo + ".png");
        //}
    }

    private void saveBoard(String fn) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                _draw.save(Board.this,"u_" + _updateNo + "_" + fn + ".png");
            }
        });
        thread.start();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
