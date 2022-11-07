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
        
         conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
        clients = new ArrayList<>();
        ServerSocket server;
        try {
            server = new ServerSocket(5555);
            while (true) {

                Socket client = server.accept();
                new serverThread(client).start();
               
    }
        } catch (IOException e) {
            e.printStackTrace();
        }

        
        }
    public static String getFullName(String username) {
        try {
            PreparedStatement ps = ServerApp.conn.prepareStatement("SELECT name FROM clients where username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            } else {
                return "not Found !";
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return "not Found !";
        }
    
    }

    public static boolean checkLogin(String username, String password) {
        try {
            PreparedStatement ps = ServerApp.conn.prepareStatement("SELECT * FROM clients where username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
    
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
          
            return false;
        }
    
    }
}
