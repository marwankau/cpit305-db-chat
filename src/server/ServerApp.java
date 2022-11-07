package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
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


                Thread thread = new Thread();
                thread.start();
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

    private static String getFullName(String username) {

        try {

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * client WHERE username = "+username);

            return rs.getString("fullName");

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return "Sample Name";
    }

    private static boolean checkLogin(String username, String password) throws SQLException  {

        PreparedStatement pr = conn.prepareStatement("Select * from client where Username LIKE ? AND Password LIKE ?");

        pr.setString(1, username);
        pr.setString(2, password);
        ResultSet rs = pr.executeQuery();

        return rs.next();
    }
}
