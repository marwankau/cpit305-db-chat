package server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Sender extends Thread {

    private Client mySelf;
    private Socket socket;

    public Sender(Client client) {
        this.mySelf = client;
    }


    public Sender(Client c, Socket client) {
        this.mySelf=c;
        this.socket=client;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = mySelf.queue.take();

                mySelf.dos.writeUTF(msg);

                if (msg.equals("bye")) {
                    break;
                }
            } catch (InterruptedException | IOException e) {
                
                System.err.println(e.getMessage());
            }
        }
        mySelf.closeSender();
        ServerApp.clients.remove(mySelf);
    }
}
