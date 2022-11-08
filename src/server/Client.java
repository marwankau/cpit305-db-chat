package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Client {
    
    private String username;
    private String fullname;
    private Socket socket;
    BlockingQueue<String> queue;
    DataInputStream dis;
    DataOutputStream dos;


    public Client(String username, String fullname, Socket socket, DataInputStream dis, DataOutputStream dos) {
        this.username = username;
        this.fullname = fullname;
        this.socket = socket;

        this.dis = dis;
        this.dos = dos;

        queue = new ArrayBlockingQueue<>(100);
    }

    public String getUsername() {
        return username;
    }
    

    public String getFullname() {
        return fullname;
    }

    public void closeSender() {
        try {
            socket.shutdownOutput();
        } catch (IOException e) { }
    }

    public void closeReceiver() {
        try {
            socket.shutdownInput();
        } catch (IOException e) { }
    }
    
}
