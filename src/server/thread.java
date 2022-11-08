package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class thread extends Thread {
    Socket client;
    public thread(Socket socket) {
        this.client = socket;

    }

    @Override
    public void run() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            DataInputStream dis = new DataInputStream(client.getInputStream());
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());

            String username = dis.readUTF();
            String password = dis.readUTF();

            md.update(password.getBytes());
            password = init.App.byte2hex(md.digest());

            if (ServerApp.checkLogin(username, password)) {
                dos.writeUTF("success");
                Client c = new Client(username, ServerApp.getFullName(username), client, dis, dos);
                ServerApp.clients.add(c);

                new Sender(c).start();
                new Receiver(c, ServerApp.clients).start();
            } else {
                dos.writeUTF("fail");
            }


        } catch (IOException e) {  System.err.println(e.getMessage());
        } catch(NoSuchAlgorithmException e ){System.err.println(e.getMessage());
        } catch (SQLException e) {System.err.println(e.getMessage());
        }
    }
}