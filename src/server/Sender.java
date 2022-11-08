package server;

import java.io.IOException;

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
            } catch (InterruptedException | IOException e) {
                
                System.err.println(e.getMessage());
            }
        }
        mySelf.closeSender();
        ServerApp.clients.remove(mySelf);
    }
}
