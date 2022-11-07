package server;

import java.beans.Statement;
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

    static String getFullName(String username) throws SQLException {
        Statement stms =  (Statement) conn.prepareStatement("SELECT name FROM clients WHERE username = ?;");
        ((PreparedStatement) stms).setString(1, username);
        ResultSet result = ((PreparedStatement) stms).executeQuery();
        return result.getString(username);

        
        

        
        
    }

    private static boolean checkLogin(String username, String password) throws SQLException {
        PreparedStatement pstms = conn.prepareStatement("SELECT * FROM clients WHERE username = ? AND password = ?;");
        pstms.setString(1, username);
        pstms.setString(2, password);
        ResultSet result = pstms.executeQuery();

        return result.next();
        
    }
}
