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

import init.App;

import java.sql.Statement;
public class ServerApp extends Thread {

    static Connection conn;
    static List<Client> clients;

    public static void main(String[] args) throws NoSuchAlgorithmException, SQLException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
        Statement stmt = conn.createStatement();
        
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

                if (checkLogin(username, password,conn)) {
                    System.out.println("true");
                    dos.writeUTF("success");
                    Client c = new Client(username, getFullName(username,conn), client, dis, dos);
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

    private static String getFullName(String username, Connection conn) throws SQLException {
        // TODO: get user's name from database using his username
        String name="";
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM `clients` WHERE `username` LIKE ? ;");

        ps.setString(1, username);
         if(ps.execute()){
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){

            String usr = rs.getString("username");
            String pass = rs.getString("password");
             name += rs.getString("name");
            System.out.printf("%10s  %32s  %100s\n", usr, pass, name);
           
        } 
        

       System.out.println(name);
    return name;
                         
      
            }

else{
     return "Sample Name";
}
         
       
    }

    private static boolean checkLogin(String username, String password, Connection conn) throws SQLException, NoSuchAlgorithmException {
        // TODO: check database for username and password
        boolean revalue=false;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        String md5_password = App.byte2hex(md.digest());
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM `clients` WHERE `username` LIKE ? ;");
        ps.setString(1, username);
       
       
       if(ps.execute()){
        ResultSet rs = ps.getResultSet();
        
        while(rs.next()){

            String usr = rs.getString("username");
            String pass = rs.getString("password");
            String name = rs.getString("name");
            System.out.printf("%10s  %32s  %100s\n", usr, pass, name);
            if(username.equals(usr) && password.equals(pass)){
                revalue=true;
                break;
            }
        } 
        

       
    return revalue;
                         
      
            }
       else {
    return  false;   
    }
     
    }
}
