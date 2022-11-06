package server;

import java.io.IOException;
import java.util.List;

public class Receiver extends Thread {
    private List<Client> clients;
    private Client mySelf;

    public Receiver(Client client, List<Client> clients) {
        this.mySelf = client;
        this.clients = clients;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = mySelf.dis.readUTF();

                if (msg.equals("bye")) {
                    mySelf.queue.put(msg);
                    break;
                }
                
                for (Client client : clients) {
                    if (client != mySelf) {
                        msg = mySelf.getFullname() + ": " + msg;
                        client.queue.put(msg);
                    }
                }
                
            } catch (InterruptedException | IOException e) {}
        }
        mySelf.closeReceiver();
    }
    
}
