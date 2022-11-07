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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class serverThread extends Thread {

    Socket client;
    MessageDigest md;
    static List<Client> clients;
    static Connection conn;

    public serverThread(Socket client, MessageDigest md, List<Client> clients, Connection conn) {
        this.client = client;
        this.md = md;
        this.clients = new ArrayList<>();
        this.conn = conn;

    }

    @Override
    public void run() {

        try {

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
            }
    
            else {
                dos.writeUTF("fail");
            }
            
        } catch (Exception e) {
            
        }

       
    }




    private static String getFullName(String username) throws SQLException {
        // TODO: get user's name from database using his username

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients where username=?");

        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            String name = rs.getString("name");
            return name;
        }

        else {
            return "Name Not found";
        }

    }

    private static boolean checkLogin(String username, String password) throws SQLException {
        // TODO: check database for username and password = done

        PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients where username=? AND password=?");

        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            return true;
        }

        else {
            return false;
        }

    }

}
