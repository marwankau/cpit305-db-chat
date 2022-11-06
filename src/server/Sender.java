package server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Sender extends Thread {

    private Client mySelf;

    public Sender(Client client) {
        this.mySelf = client;
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
            } catch (InterruptedException | IOException e) {}
        }
        mySelf.closeSender();
        ServerApp.clients.remove(mySelf);
    }
}
