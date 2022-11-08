package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Mythreads extends Thread {
    private Socket clien;

    Mythreads(Socket clien) {
        this.clien = clien;
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
            dis = new DataInputStream(clien.getInputStream());
            dos = new DataOutputStream(clien.getOutputStream());

            String username = dis.readUTF();
            String password = dis.readUTF();

            md.update(password.getBytes());
            password = init.App.byte2hex(md.digest());

            if (ServerApp.checkLogin(username, password)) {
                dos.writeUTF("success");
                Client c = new Client(username, ServerApp.getFullName(username), clien, dis, dos);
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