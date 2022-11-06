package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Receiver extends Thread {

    private DataInputStream dis;
    private Socket socket;

    public Receiver(DataInputStream dis, Socket socket) {
        this.dis = dis;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String msg = dis.readUTF();
                if (msg.equals("bye")) {
                    break;
                }
                System.out.println(msg);
            }

        } catch (IOException e) { 

        } finally {
            try {
                socket.shutdownInput();
            } catch (IOException e) { }
        }
    }
    
}
