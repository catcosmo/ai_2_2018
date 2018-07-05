import lenz.htw.zpifub.*;
import lenz.htw.zpifub.net.NetworkClient;

public class Client {

    NetworkClient client = null;

    public Client() { }

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

    public NetworkClient getRemoteClient() {
        return client;
    }

    private void runClient(){
        client = new NetworkClient("localhost", "wholetrain", "...kann ich jetzt gehen?");

        int player = client.getMyPlayerNumber(); // 0 = rot, 1 = grün, 2 = blau

        //long score = client.getScore(0); // Punkte von rot
        // client.getInfluenceRadiusForBot(0); // gibt 40
        // client._isWalkable(x, y); // begehbar oder Hinderniss?
        //int rgb = client.getBoard(x, y); // x,y zwischen 0-1023
        //int b = rgb & 255;
        //int g = (rgb >> 8) & 255;
        //int r = (rgb >> 16) & 255;

        Board board = new Board(this);
        board.initBoard();

        PowerUps powerUps = new PowerUps();

        Bot bot0 = new Bot(0,1,0); // bot 0 nach rechts
        Bot bot1 = new Bot(1,1,0); // bot 1 nach rechts unten
        Bot bot2 = new Bot(2,0,1); // bot 1 nach rechts unten

        while( client.isAlive() ) {
            Update update;
            while ((update = client.pullNextUpdate()) != null) {

                board.updateBoard(update);
                powerUps.update(update);
                bot0.updatePos(update, board, this);
                bot1.updatePos(update, board, this);
                bot2.updatePos(update, board, this);

                bot0.move(board, this);
                bot1.move(board, this);
                bot2.move(board, this);

            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
    }
}
