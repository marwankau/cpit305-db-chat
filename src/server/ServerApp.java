package server;

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

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
        clients = new ArrayList<>();
        try (ServerSocket server = new ServerSocket(5555)) {

            while (true) {

                Socket client = server.accept();

                LoginThred log = new LoginThred (md,conn,clients,client);
                log.start();
       
                }
            }
             catch (IOException e) {
                System.err.println(e.getMessage());
             }
    }
}