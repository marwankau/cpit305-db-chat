package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        Socket socket = new Socket("localhost", 5555);

        DataInputStream dis = new DataInputStream(socket.getInputStream());
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

        Scanner keyboard = new Scanner(System.in);

        System.out.print("username: ");
        String username = keyboard.nextLine();

        System.out.print("password: ");
        String password = keyboard.nextLine();

        dos.writeUTF(username);
        dos.writeUTF(password);

        String res = dis.readUTF();

        if (res.equals("success")) {
            // create two threads one for receiveing and one for sending
            Sender sender = new Sender(dos, socket);
            Receiver receiver = new Receiver(dis, socket);

            sender.start();
            receiver.start();

            sender.join();
            receiver.join();
        } else {
            System.out.println("Wrong username or password!");
        }

        keyboard.close();
        socket.close();
    }
}
