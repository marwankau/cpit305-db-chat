package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
/*
 * this Thread class will get make the server accept serval clients at the same time without any corrupt or waiting
 * using same methods but instead of make run() method do it all, I created a method LoginMethod() to do all the login and run() calls it
 * due to run() signature cannot be changed
 */

public class Login extends Thread {
    private Socket socket;
    private List<Client> arrClients;
    private static Connection conn;
    
    public Login(Connection conn, Socket socket, List<Client> arrClients) {
        this.conn = conn;
        this.socket = socket;
        this.arrClients = arrClients;
    }

    @Override
    public void run() {
        try {
            LoginMethod();
        } catch (NoSuchAlgorithmException | IOException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void LoginMethod() throws NoSuchAlgorithmException, IOException, SQLException {
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        String username = dis.readUTF();
        String password = dis.readUTF();

        if (checkLogin(username, password)) {
            dos.writeUTF("success");
            Client c = new Client(username, getFullName(username), socket, dis, dos);
            arrClients.add(c);
            new Sender(c).start();
            new Receiver(c, arrClients).start();
        } else {
            dos.writeUTF("fail");
        }
    }

    private static String getFullName(String username) throws SQLException, NoSuchAlgorithmException {
        Statement statment = conn.createStatement();
        ResultSet result = statment.executeQuery("SELECT * FROM clients;");

        while (result.next()) {
            String userTemp = result.getString("username");
            if (userTemp.equals(username)) {
                return result.getString("name");
            }
        }
        return null;
    }

    private static boolean checkLogin(String username, String password) throws SQLException, NoSuchAlgorithmException {
        Statement statment = conn.createStatement();
        ResultSet result = statment.executeQuery("SELECT * FROM clients;");
        MessageDigest md = MessageDigest.getInstance("MD5");

        String passTemp;

        while (result.next()) {
            String userTemp = result.getString("username");
            if (userTemp.equals(username)) {
                passTemp = result.getString("password");

                md.update(password.getBytes());
                String passwordDigest = init.App.byte2hex(md.digest());

                if (passTemp.equals(passwordDigest))
                    return true;
            }
        }
        return false;
    }
}
