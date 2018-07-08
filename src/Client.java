import lenz.htw.zpifub.*;
import lenz.htw.zpifub.net.NetworkClient;

public class Client {

    NetworkClient client = null;
    boolean _dummyClient = false;

    public Client() { }
    public Client(boolean dummy) { _dummyClient=dummy; }

    public Thread startPlayLoop() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runClient();
            }
        });
        thread.start();
        return thread;
    }

    public Thread startSaveLoop(final Board board) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while( client.isAlive() ) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    //board.saveBoard();
                }
            }
        });
        thread.start();
        return thread;
    }

    public NetworkClient getRemoteClient() {
        return client;
    }

    private void runClient(){
        client = new NetworkClient("localhost", "wholetrain", "...kann ich jetzt gehen?");
        //client = new NetworkClient("10.1.12.145", "oshie", "...YEAH?");

        int player = client.getMyPlayerNumber(); // 0 = rot, 1 = grün, 2 = blau

        //long score = client.getScore(0); // Punkte von rot
        // client.getInfluenceRadiusForBot(0); // gibt 40
        // client._isWalkable(x, y); // begehbar oder Hinderniss?
        //int rgb = client.getBoard(x, y); // x,y zwischen 0-1023
        //int b = rgb & 255;
        //int g = (rgb >> 8) & 255;
        //int r = (rgb >> 16) & 255;

        if( _dummyClient ) {
            while( client.isAlive() ) {
                Update update;
                while ((update = client.pullNextUpdate()) != null) {

                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            return;
        }

        Board board = new Board(this);
        board.initBoard();
        //RasterNode[] rasterNodes = board.getRaster(128);

        PowerUps powerUps = new PowerUps(board);

        Bot bot0 = new Bot(0, 1, 0); // bot 0 nach rechts
        Bot bot1 = new Bot(1, 1, 0); // bot 1 nach rechts unten
        Bot bot2 = new Bot(2, 0, 1); // bot 1 nach rechts unten

        //Thread saveLoop = startSaveLoop(board);

        while( client.isAlive() ) {
            Update update;
            while ((update = client.pullNextUpdate()) != null) {

                board.updateBoard(update);
                powerUps.update(update);
                bot0.updatePos(update, board, this);
                //bot1.updatePos(update, board, this);
                //bot2.updatePos(update, board, this);
            }

            // move AFTER all updates are processed
            bot0.move(board, this, powerUps);
            //bot1.move(board, this, powerUps);
            //bot2.move(board, this, powerUps);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

//        try {
//            saveLoop.join();
//        } catch (InterruptedException e) {
//        }
    }
}

