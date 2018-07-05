import lenz.htw.zpifub.*;

public class Main {
    public static void main(String args[]) {
        //String hostName = "localhost";

        Client client1 = new Client();
        Client client2 = new Client();
        Client client3 = new Client();

        Thread thread1 = client1.startPlayLoop();
        Thread thread2 = client2.startPlayLoop();
        Thread thread3 = client3.startPlayLoop();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
        }

        //start Game
        //for (int i = 1; i <= 3; i++) {
        //String teamName = "Team " + i;
        //Client client = new Client(hostName, teamName, "Yeah!");
        //Thread thread = new Thread(client);
        //thread.start();
        //}
    }
}
