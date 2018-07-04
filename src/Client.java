import lenz.htw.zpifub.*;
import lenz.htw.zpifub.net.NetworkClient;

public class Client {

    public void moveClient(){

    NetworkClient client = new NetworkClient("localhost", "wholetrain", "If it takes more than 5 minutes, its not graffiti.");
    int player = client.getMyPlayerNumber(); // 0 = rot, 1 = grün, 2 = blau

    long score = client.getScore(0); // Punkte von rot

    // client.getInfluenceRadiusForBot(0); // gibt 40


    // client.isWalkable(x, y); // begehbar oder Hinderniss?

    //int rgb = client.getBoard(x, y); // x,y zwischen 0-1023
    //int b = rgb & 255;
    //int g = (rgb >> 8) & 255;
    //int r = (rgb >> 16) & 255;

    client.setMoveDirection(0,1,0); // bot 0 nach rechts
    client.setMoveDirection(1,0.23f,-0.52f); // bot 1 nach rechts unten

    Update update;
    while((update =client.pullNextUpdate())!=null)

    {
        //verarbeite update
        if (update.type == null) {
            bot[update.player][update.bot].pos = update.x, update.y;
        } else if (update.player == -1) {
            //update spawned, type, position
        } else {
            //update collected
        }
    }
}
}
