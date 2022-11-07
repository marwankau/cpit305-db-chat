package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.SQLException;
public class thread extends Thread {
Socket client;
MessageDigest md;
Connection conn;
public thread(Socket client, MessageDigest md, Connection conn) {
    this.client = client;
    this.md = md;
    this.conn = conn;
}
    @Override
    public void run() {


                // TODO: make server accept login check for several clients in the same time

                try (DataInputStream dis = new DataInputStream(client.getInputStream())) {
                    try (DataOutputStream dos = new DataOutputStream(client.getOutputStream())) {
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
                    } catch (IOException | SQLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

    }
}


