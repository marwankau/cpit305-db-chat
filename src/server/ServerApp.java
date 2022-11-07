package server;


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

                new thread(client, md, conn).start();

            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    static String getFullName(String username) throws SQLException {
        PreparedStatement ps =  conn.prepareStatement("SELECT name FROM clints where username=?"); 
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        return rs.getString("name");
        
       
    }

    static boolean checkLogin(String username, String password) {
        try {
            PreparedStatement ps =  conn.prepareStatement("SELECT FROM clints where username=? AND password=?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {}
        return false;
    }
}
