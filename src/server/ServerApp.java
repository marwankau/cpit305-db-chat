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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
                serverThread thread = new serverThread(client, md);
                thread.start();

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static String getFullName(String username) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
            PreparedStatement ps = conn.prepareStatement("SELECT name FROM clients WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.getString("name");
        } catch (Exception e) {
        }
        return "wrong name";
    }

    public static boolean checkLogin(String username, String password) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");

            PreparedStatement ps = conn
                    .prepareStatement("SELECT username FROM clients WHERE username =? AND password = ?");

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            String userName = rs.getString("username");
            if (userName != null)
                return true;

        } catch (Exception e) {
        }
        return false;
    }
}