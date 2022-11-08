package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class serverThread extends Thread {
    Socket client;
    MessageDigest md;
    static Connection conn;

    public serverThread(Socket client, MessageDigest md) {
        this.client = client;
        this.md = md;
    }

    public void run() {
        try {
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
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