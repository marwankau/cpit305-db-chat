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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {

    static Connection conn;
    static List<Client> clients;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, SQLException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
        clients = new ArrayList<>();
        ServerSocket server = new ServerSocket(5555);

        while (true) {

            Socket client = server.accept();

            // TODO: make server accept login check for several clients in the same time

            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());

            String username = dis.readUTF();
            String password = dis.readUTF();

            md.update(password.getBytes());
            password = init.App.byte2hex(md.digest());

            if (checkLogin(username, password)) {
                dos.writeUTF("success");
                Client c = new Client(username, getFullName(username), client, dis, dos);
                clients.add(c);

                new Sender(c).start();
                new Receiver(c, clients).start();
            } else {
                dos.writeUTF("fail");
            }



            client.close();
        }
    }

    private static String getFullName(String username) {
        // TODO: get user's name from database using his username 
        return "Sample Name";
    }

    private static boolean checkLogin(String username, String password) {
        // TODO: check database for username and password
        return true;
    }
}
