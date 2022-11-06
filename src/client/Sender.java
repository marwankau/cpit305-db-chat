package client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Sender extends Thread {

    private DataOutputStream dos;
    private Socket socket;

    public Sender(DataOutputStream dos, Socket socket) {
        this.dos = dos;
        this.socket = socket;
    }

    @Override
    public void run() {
        Scanner keyboard = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("msg: ");
                String msg = keyboard.nextLine();

                dos.writeUTF(msg);
                if (msg.equals("bye")) {
                    break;
                }
            }

        } catch (IOException e) { 

        } finally {
            keyboard.close();
            try {
                socket.shutdownOutput();
            } catch (IOException e) { }
        }
    }    
}
