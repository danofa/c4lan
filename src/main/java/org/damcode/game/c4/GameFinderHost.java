package org.damcode.game.c4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dm
 */
public class GameFinderHost extends Thread {

    private boolean running = true;
    public static final int PORT = 5056;
    public static final int BROADCAST_TIME = 5000;

    DatagramSocket socket;
    private byte[] hsMsg = "DAMC4SRV".getBytes();

    public static void main(String[] args) {
        new GameFinderHost().start();
    }

    @Override
    public void run() {
        while (running) {
            try {

                socket = new DatagramSocket();
                InetAddress address = Inet4Address.getByName("231.0.0.1");
                DatagramPacket packet;
                packet = new DatagramPacket(hsMsg, hsMsg.length, address, 5056);
                socket.send(packet);
                System.out.println("Sent packet.....");
                Thread.sleep(BROADCAST_TIME);

            } catch (Exception ex) {
                System.out.println("Socket class failed creation: " + ex);
                Logger.getLogger(GameFinderHost.class.getName()).log(Level.SEVERE, null, ex);
            }
            socket.close();
        }
    }

    public void start() {
        new Thread(this).start();
    }

    public void shutdown() {
        System.out.println("Host finder stopped broadcasting");
        socket.close();
        running = false;
    }
}
