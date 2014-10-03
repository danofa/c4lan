package org.damcode.game.c4;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 *
 * @author dm
 */
public class GameFinderClient implements Runnable {

    private static final String HS_MSG = "DAMC4SRV";
    private InetAddress srvAddr;
    private boolean waiting = true;
    private static final String MULTICAST_GROUP = "231.0.0.1";
    private MulticastSocket socket;
    private InetAddress address;
    private static final int PORT = 5056;

    public String getServer() {
        return srvAddr.getHostAddress();
    }
    
    public boolean isWaiting(){
        return waiting;
    }

    @Override
    public void run() {
        byte[] buf = new byte[256];

        while (isWaiting()) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket = new MulticastSocket(PORT);
                address = InetAddress.getByName(MULTICAST_GROUP);
                socket.joinGroup(address);

                socket.receive(packet);

                String rcv = new String(packet.getData()).trim();

                if (HS_MSG.equals(rcv)) {
                    srvAddr = packet.getAddress();
                    waiting = false;
                    System.out.println("Server found!");
                }

            } catch (Exception ex) {
                System.out.println("Closed client finder socket!" + ex);
            }

        }
        stop();
    }

    public void start() {
        new Thread(this).start();
    }

    public void stop() {
        waiting = false;
        socket = null;
    }
}
