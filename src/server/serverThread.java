package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class serverThread extends Thread {
  Socket client;
  MessageDigest md;
  static Connection conn;

  public serverThread(Socket client, MessageDigest md) {
    this.client = client;
    this.md = md;
  }

  @Override
  public void run() {
    
      try  {
        DataInputStream dis = new DataInputStream(client.getInputStream());
        DataOutputStream dos = new DataOutputStream(client.getOutputStream());
        String username = dis.readUTF();
        String password = dis.readUTF();

        md.update(password.getBytes());
        password = init.App.byte2hex(md.digest());

        if (checkLogin(username, password)) {
          dos.writeUTF("success");
          Client c = new Client(username, getFullName(username), client, dis, dos);
          ServerApp.clients.add(c);

          new Sender(c).start();
          new Receiver(c, ServerApp.clients).start();
        } else {
          dos.writeUTF("fail");
        }
      } catch (IOException e) {
      }
    }

  private static String getFullName(String username) {
    try {
      conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
      PreparedStatement ps = conn.prepareStatement("SELECT name FROM clients WHERE username = ?;");
      ps.setString(1, username);
      ResultSet rs = ps.executeQuery();
      return rs.getString("name");
    } catch (Exception e) {
    }
    return "Invalid name";
  }

  private static boolean checkLogin(String username, String password) {

    try {
      conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
      // prepare sql statement
      PreparedStatement ps = conn.prepareStatement("SELECT username FROM clients WHERE username = ? AND password = ?;");
      // passing value into the sql
      ps.setString(1, username);
      ps.setString(2, password);
      ResultSet rs = ps.executeQuery();
      String userName = rs.getString("username");
      if (userName != null)
        return true;

    } catch (Exception e) {
    }
    return false;
  }
}