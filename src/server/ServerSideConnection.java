package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

class ServerSideConnection extends Thread {

    static MessageDigest md;
    static Connection conn;
    static List<Client> clients;
    static Socket client;

    ServerSideConnection(MessageDigest md, Connection conn, List<Client> clients, Socket client) {
        this.md = md;
        this.conn = conn;
        this.clients = clients;
        this.client = client;

    }

    @Override
    public void run() {
        try {
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
        } catch (IOException e) {
            System.err.println("Problem in server side connection!");
        }

        catch (SQLException e) {
            System.err.println("Problem the Database!");
        }

    }

    private static String getFullName(String username) throws SQLException {
        // TODO: get user's name from database using his username

        PreparedStatement ps = conn.prepareStatement("select name from clients where username = ?");

        ps.setString(1, username);
        ps.execute();
        ResultSet r = ps.getResultSet();
        if (r.next()) {
            return r.getString("name");
        }

        return "Not found";
    }

    private static boolean checkLogin(String username, String password) throws SQLException {
        // TODO: check database for username and password
        PreparedStatement ps = conn.prepareStatement("select name from clients where username = ? and password = ?");

        ps.setString(1, username);
        ps.setString(2, password);
        ps.execute();
        ResultSet r = ps.getResultSet();
        if (r.next()) {
            return true;
        }

        return false;

    }
}
 