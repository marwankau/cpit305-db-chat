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

import javax.naming.spi.DirStateFactory.Result;

public class ServerApp {

    static Connection conn;
    static List<Client> clients;

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md = MessageDigest.getInstance("MD5");
         conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");

        clients = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(5555)) {

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
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static String getFullName(String username) throws SQLException {
        // TODO: get user's name from database using his username
       String name;
       PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients WHERE username LIKE ?;");
       ps.setString(1,"%"+ username+"%");
       if (ps.execute()) {
        ResultSet rs = ps.getResultSet();
        while(rs.next()){
            name = rs.getString("name");
            return name;
        }
      
       }
       
      return null;
    }

    private static boolean checkLogin(String username, String password) throws SQLException {
        // TODO: check database for username and password
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM clients where username=? AND password=?");

        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (ps.execute()) {
            rs = ps.getResultSet();
            while (rs.next()) {
                if (rs.getString("username").equalsIgnoreCase(username)
                        && rs.getString("password").equalsIgnoreCase(password)) {
                    return true;
                }
            }
        }
        return false;
    }
}
