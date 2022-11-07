package server;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Receiver extends Thread {
    private List<Client> clients;
    private Client mySelf;
    private Socket socket;

    public Receiver(Client client, List<Client> clients) {
        this.mySelf = client;
        this.clients = clients;
    }

    public Receiver(Client c, List<Client> clients2, Socket client) {
        this.mySelf=c;
        this.clients = clients;
        this.socket=client;
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
                        client.queue.put(mySelf.getFullname() + ": " + msg);
                    }
                }
                
            } catch (InterruptedException | IOException e) {
                
                System.err.println(e.getMessage());
            }
        }
        mySelf.closeReceiver();
    }
    
}
