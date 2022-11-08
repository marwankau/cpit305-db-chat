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

                // TODO: make server accept login check for several clients in the same time
                ServerSideConnection ssc = new ServerSideConnection(md,conn,clients,client);
                ssc.start();
       
                }
            }
             catch (IOException e) {
                System.err.println("Problem in accepting connections!");
            }
    }
}
            
            
