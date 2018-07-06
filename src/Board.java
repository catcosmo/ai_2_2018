import lenz.htw.zpifub.Update;

import static lenz.htw.zpifub.PowerupType.RAIN;
import static lenz.htw.zpifub.PowerupType.SLOW;

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
            // if PU is CLOCK (so should be avoided)
//            // make 20px non walkable barrier around it so that the bots don't hit it
            if(_board[update.x][update.y]._puType==SLOW){
                for (int x = update.x-20; x <= update.x+20 ; x++) {
                    for (int y = update.y-20; y <= update.y+20; y++) {
                        _board[x][y]._tempBlock = true;
                        System.out.println("TenpBlockAdd x:"+x+"y:"+y);

                    }
                }
            }
        }else if (update.player > -1 && update.bot > -1){
            // place player
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y]._bot = update.bot;
        } else if (update.player > -1 && update.bot == -1){
            // delete PowerUp and update player position
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y]._puType = null;
            _board[update.x][update.y]._hasPU = false;
            // clean up CLOCK barrier
            if(_board[update.x][update.y]._tempBlock){
                System.out.println("TenpBlockRemove x:"+update.x+"y:"+update.y);

                for (int x = update.x-20; x <= update.x+20 ; x++) {
                    for (int y = update.y - 20; y <= update.y + 20; y++) {
                        _board[x][y]._tempBlock = false;
                    }
                }
            }
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


    // calculate rasterNodes and store in List
    public RasterNode[] getRaster(int rasterSize) {
        int counter = 0;
        RasterNode[] rasterNodes = new RasterNode[(_board.length/rasterSize)*(_board.length/rasterSize)];
        for (int y = 0; y < 1024; y+=rasterSize) {
            for (int x = 0; x < 1024; x+=rasterSize) {
                int weight = calcRasterWeight(x, y, rasterSize);
                System.out.println("Weight for Raster" + counter + "is" + weight);
                RasterNode rasterNode = new RasterNode(x, y, rasterSize, counter, weight);
                counter+=1;
            }
        }
        return rasterNodes;
    }

    //calculate _weight colour (or weight??) of raster node
    private int calcRasterWeight(int startX, int startY, int rasterSize) {
        int weight = 0;
        int rgb;
        int r = 0; //r, g, b total sum of all fields - the higher, the better the area for that color
        int g = 0;
        int b = 0;
        int s = 0; // how many black blocks, numerical
        int w = 0; // how many white blocks, numerical
        int rTemp;
        int gTemp;
        int bTemp;

        // go through raserNode pixel by pixel
        for (int y = startY; y < rasterSize; y++) {
            for(int x = startX; x < rasterSize; x++) {
                //extract color as int 0-255
                rgb = _client.getRemoteClient().getBoard(x, y);
                bTemp = rgb & 255;
                gTemp = (rgb >> 8) & 255;
                rTemp = (rgb >> 16) & 255;
                // count black & white pixels
                if (rTemp==bTemp && bTemp==gTemp && rTemp == 0){
                    s+=1;
                }
                else if (rTemp==bTemp && bTemp==gTemp && rTemp == 255){
                    w+=255;
                }
                //sum up color values for the other colors
                else {
                    //odont add value for fields in own color (more than 125 of own color)
                    switch (_client.getRemoteClient().getMyPlayerNumber()) {
                        case 0:
                            if(rTemp <= 125)
                                r+=rTemp;
                            b += bTemp;
                            g += gTemp;
                            break;
                        case 1:
                            if(gTemp <= 125)
                                g+=gTemp;
                            b += bTemp;
                            r+=rTemp;
                            break;
                        case 2:
                            if(bTemp <= 125)
                                b+=bTemp;
                            r+=rTemp;
                            g += gTemp;
                            break;
                    }

                    r += (rgb >> 16) & 255;
                }
                //TODO finish method & logic
            }
        }
        //current weighting: white is more important than colors, black doesnt give negative values
        weight = r+g+b+w;
        return weight;
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
