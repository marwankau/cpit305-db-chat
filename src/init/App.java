package init;

import java.sql.Statement;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App {
  public static void main(String[] args) throws SQLException, NoSuchAlgorithmException {
    MessageDigest md = MessageDigest.getInstance("MD5");
    Connection conn = DriverManager.getConnection("jdbc:sqlite:src/server/data.db");
    Statement stmt = conn.createStatement();

    stmt.execute("""
        CREATE TABLE IF NOT EXISTS `clients` (
          `username` varchar(10) NOT NULL,
          `password` varchar(32) NOT NULL,
          `name` varchar(100) DEFAULT NULL,
          PRIMARY KEY (`username`)
        );
            """);

    md.update("123".getBytes());
    String md5_password = byte2hex(md.digest());

    PreparedStatement ps = conn
        .prepareStatement("INSERT INTO `clients` (`username`, `password`, `name`) VALUES (?, ?, ?);");
    ps.setString(1, "ahmed");
    ps.setString(2, md5_password);
    ps.setString(3, "Ahmed Khalid");
    ps.executeUpdate();

    md.update("222".getBytes());
    md5_password = byte2hex(md.digest());

    ps.setString(1, "fahad");
    ps.setString(2, md5_password);
    ps.setString(3, "Fahad Abdullah");
    ps.executeUpdate();
  }

  public static String byte2hex(byte[] digest) {
    StringBuilder hex = new StringBuilder();

    for (byte b : digest) {
      hex.append(String.format("%02x", b));
    }

    return hex.toString();
  }
}
