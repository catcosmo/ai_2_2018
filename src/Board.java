import lenz.htw.zpifub.Update;

import static lenz.htw.zpifub.PowerupType.SLOW;

public class Board implements Cloneable {
    Field[][] _board = new Field[1024][1024];
    Client _client = null;
    DrawPanel _draw = new DrawPanel();
    public static int _updateNo = 0;

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
                if( !field._isWalkable ) {
                    field._color=Field.BLACK;
                }
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
            //saveBoard("PU");
            // if PU is CLOCK (so should be avoided)
//            // make 20px non walkable barrier around it so that the bots don't hit it
            if(_board[update.x][update.y]._puType==SLOW){
                for (int x = update.x-20; x <= update.x+20 ; x++) {
                    for (int y = update.y-20; y <= update.y+20; y++) {
                        _board[x][y]._tempBlock = true;
                        // System.out.println("TenpBlockAdd x:"+x+"y:"+y);
                    }
                }
            }
        }else if (update.player > -1 && update.bot > -1){
            // place player
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y]._bot = update.bot;
            _board[update.x][update.y].setColorFromPlayer(update.player);
        } else if (update.player > -1 && update.bot == -1){
            // delete PowerUp and update player position
            _board[update.x][update.y]._player = update.player;
            _board[update.x][update.y].setColorFromPlayer(update.player);
            _board[update.x][update.y]._puType = null;
            _board[update.x][update.y]._hasPU = false;
            // clean up CLOCK barrier
            if(_board[update.x][update.y]._tempBlock){
                // System.out.println("TempBlockRemove x:"+update.x+"y:"+update.y);

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


    //calculate hot area for bot
    public RasterNode getHotArea(RasterNode[] rasterNodes, Bot bot){
        if( bot._pos==null )
            return rasterNodes[0];

        int botRadius = bot._radius;
        int best = 0;
        int hottestArea = 1;
        int closest = 0;
        for (int i = 0; i < rasterNodes.length; i++) {
            //if brush is slow, check nearer neighbourhood only
            //spraycan gets the whole field to play in
            //small brush
            if(botRadius == 15 &&
                    ((bot._pos._x-rasterNodes[i].get_startX())>512
                        || (bot._pos._y-rasterNodes[i].get_startY())>512)){
                continue;
            }
            //wide brush
            if(botRadius == 30 &&
                    ((bot._pos._x-rasterNodes[i].get_startX())>400
                            || (bot._pos._y-rasterNodes[i].get_startY())>400)){
                continue;
            }
            if(rasterNodes[i].get_weight()>best) {
                best = rasterNodes[i].get_weight();
                closest = (bot._pos._x-rasterNodes[i].get_startX())+(bot._pos._y-rasterNodes[i].get_startY());
                hottestArea = i;

            }
            //get NEAREST hottest field: if rasterNode is equally good, take the closer one
            else if (rasterNodes[i].get_weight()==best){
                if(closest > (bot._pos._x-rasterNodes[i].get_startX())+(bot._pos._y-rasterNodes[i].get_startY())){
                    best = rasterNodes[i].get_weight();
                    closest = (bot._pos._x-rasterNodes[i].get_startX())+(bot._pos._y-rasterNodes[i].get_startY());
                    hottestArea = i;
                }
            }
        }
        System.out.println("Hottest Area for this bot is: " + rasterNodes[hottestArea].get_numberID());
        return rasterNodes[hottestArea];
    }

    //return raster field ID of current position
    public int getRasterID(int x, int y, int rasterSize){
        int rasterID;
        int xID;
        int yID;
        int axisRasterNo = 1024/rasterSize;

        // calc xPos
        if(x%rasterSize==0)
            xID = x/rasterSize;
        else xID = x/rasterSize +1;

        //calc yPos
        if(y%rasterSize==0)
            yID = y/rasterSize;
        else yID = y/rasterSize +1;

        //add xPos & yPos to calculate field
        rasterID = xID + ((yID-1)*axisRasterNo);
        return rasterID;
    }


    // calculate rasterNodes and store in List
    public RasterNode[] getRaster(int rasterSize) {
        int counter = 0;
        RasterNode[] rasterNodes = new RasterNode[(_board.length/rasterSize)*(_board.length/rasterSize)];
        for (int y = 0; y < 1024; y+=rasterSize) {
            for (int x = 0; x < 1024; x+=rasterSize) {
                int weight = calcRasterWeight(x, y, rasterSize, 0);
                //System.out.println("Weight for Raster:" + counter + " is:" + weight);
                RasterNode rasterNode = new RasterNode(x, y, rasterSize, counter, weight);
                rasterNodes[counter] = rasterNode;
                counter+=1;
            }
        }
        return rasterNodes;
    }

    //calculate _weight colour (or weight??) of raster node
    //avoidBlackFactor: factor with which black pixels are multiplied:
    //for hotAreaDtection 0, for pathfinding (smaller raster) e.g. 1000
    private int calcRasterWeight(int startX, int startY, int rasterSize, int avoidBlackFactor) {
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
        for (int y = startY; y < startY + rasterSize; y++) {
            for(int x = startX; x < startX + rasterSize; x++) {
                //extract color as int 0-255
                //rgb = getField(x,y)._color;
                rgb = _client.getRemoteClient().getBoard(x,y);
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
                                r += rTemp;
                                b += bTemp*3;
                                g += gTemp*3;
                            break;
                        case 1:
                            if(gTemp <= 125)
                                g += gTemp;
                                b += bTemp*3;
                                r += rTemp*3;
                            break;
                        case 2:
                            if(bTemp <= 125)
                                b+=bTemp;
                                r += rTemp*3;
                                g += gTemp*3;
                            break;
                    }
                }
            }
        }
        //current weighting: white is more important than colors, black doesnt give negative values
        s = s*avoidBlackFactor*-1;
        weight = r+g+b+w+s;
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
