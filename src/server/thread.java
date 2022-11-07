package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class thread extends Thread {
    private Socket client;

    thread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }
    
        try {    
            DataInputStream dis =  new DataInputStream(client.getInputStream());
            DataOutputStream dos = dos = new DataOutputStream(client.getOutputStream());

            String username = dis.readUTF();
            String password = dis.readUTF();

            md.update(password.getBytes());
            password = init.App.byte2hex(md.digest());

            if (ServerApp.checkLogin(username, password)) {
                dos.writeUTF("complete");
                Client c = new Client(username, ServerApp.getFullName(username), client, dis, dos);
                ServerApp.clients.add(c);

                new Sender(c).start();
                new Receiver(c, ServerApp.clients).start();
            } else {
                dos.writeUTF("Wreong ");
            }
        } catch (IOException e) {
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
