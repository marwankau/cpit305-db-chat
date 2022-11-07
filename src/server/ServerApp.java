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

public class ServerApp {

    static Connection conn;
    static List<Client> clients;

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
        clients = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(5555)) {

            while (true) {

                Socket client = server.accept();
                MyThread T = new MyThread(client,md,conn);
                T.start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    static String getFullName(String username) throws SQLException {
        String fname;
        PreparedStatement ps = conn.prepareStatement("SELECT name FROM clients WHERE username LIKE ?;");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        fname = rs.getString("name");
        return fname;
    }

    static boolean checkLogin(String username, String password) {
        boolean x = check(true, username, password);
        return x;
    }

    static boolean check(boolean x, String username, String password) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT username AND password FROM clients WHERE username LIKE ? AND password LIKE ?;");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            rs.next();
            x = true;
        } catch (SQLException e) {
            x = false;
        } finally {
            return x;
        }
    }
}