package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class myThread extends Thread {
    private Socket client;

    myThread(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
        }

        DataInputStream dis = null;
        DataOutputStream dos = null;
        try {
            dis = new DataInputStream(client.getInputStream());
            dos = new DataOutputStream(client.getOutputStream());

            String username = dis.readUTF();
            String password = dis.readUTF();

            md.update(password.getBytes());
            password = init.App.byte2hex(md.digest());

            if (ServerApp.checkLogin(username, password)) {
                dos.writeUTF("success");
                Client c = new Client(username, ServerApp.getFullName(username), client, dis, dos);
                ServerApp.clients.add(c);

                new Sender(c).start();
                new Receiver(c, ServerApp.clients).start();
            } else {
                dos.writeUTF("fail");
            }
        } catch (IOException e) {
        }

    }
}