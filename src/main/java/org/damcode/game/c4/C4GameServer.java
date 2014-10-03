package org.damcode.game.c4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class C4GameServer extends Thread {

    private ServerSocket serverSocket;
    private static final int PORT = 5065;
    private boolean serving = false;
    private boolean running = true;
    private GameFinderHost hostfinder;

    void shutdown() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

            if (hostfinder != null) {
                hostfinder.shutdown();
            }

        } catch (IOException ex) {
            System.out.println("trying to shutdown game server" + ex);
            Logger.getLogger(C4GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    boolean isServing() {
        return serving;
    }

    @Override
    public void run() {
        List<PlayerSocket> playerSockets = new ArrayList<PlayerSocket>();
        int numsocks = 0;
        hostfinder = null;

        try {
            serverSocket = new ServerSocket(PORT);

            main:
            while (running) {
                boolean gotdisconnect = false;

                for (PlayerSocket ps : playerSockets) {
                    if (ps.getSocket().isClosed()) {
                        playerSockets.remove(ps);
                        numsocks--;
                        System.out.println("removed closed socket");
                        gotdisconnect = true;
                        break;
                    }
                }

                if (gotdisconnect) {
                    for (PlayerSocket p : playerSockets) {
                        new PrintWriter(p.getSocket().getOutputStream(), true).println("DCD");
                    }
                    continue;
                }

                if (numsocks < 2) {
                    serving = false;

                    if (hostfinder == null) {
                        hostfinder = new GameFinderHost();
                        hostfinder.start();
                        System.out.println("Started host broadcaster");
                    }

                    System.out.println("not enough clients, waiting for more");

                    if (serverSocket != null) {
                        PlayerSocket ps = new PlayerSocket(serverSocket.accept());
                        playerSockets.add(ps);
                        ps.start();
                        numsocks++;
                    }

                } else {
                    serving = true;
                    gotdisconnect = false;

                    if (hostfinder != null) {
                        hostfinder.shutdown();
                        hostfinder = null;
                    }

                    for (int i = 0; i < playerSockets.size(); i++) {
                        PlayerSocket ps = playerSockets.get(i);
                        PrintWriter out = new PrintWriter(playerSockets.get(i ^ 1).getSocket().getOutputStream(), true);
                        ConcurrentLinkedQueue<String> msgs = ps.getMessages();
                        String s;
                        while ((s = msgs.poll()) != null) {
                            if (out.checkError())
                                System.out.println("PrintWriter Error");
                            
                            System.out.println("sent: " + s + ", to: " + (i ^ 1) + " : " + msgs.isEmpty());
                            out.println(s);

                        }
                    }

                    // ready to start main game screen;
                    int readycount = 0;
                    for (PlayerSocket p : playerSockets) {
                        if (!p.ready)
                            break;
                        if (p.ready) {
                            readycount++;
                        }
                    }

                    if (readycount == 2) {
                        for (PlayerSocket p : playerSockets) {
                            new PrintWriter(p.getSocket().getOutputStream(), true).println("RDY");
                            p.ready = false;
                            System.out.println("ready false");
                        }
                    }

                    // rendering game graphics screen.
                    int playingcount = 0;
                    for (PlayerSocket p : playerSockets) {
                        if (!p.playing)
                            break;
                        if (p.playing) {
                            playingcount++;
                        }
                    }

                    if (playingcount == 2) {
                        Random r = new Random();
                        System.out.println("randomising starting player");
                        if (r.nextBoolean()) {
                            new PrintWriter(playerSockets.get(0).getSocket().getOutputStream(), true).println("YOU");
                        } else {
                            new PrintWriter(playerSockets.get(1).getSocket().getOutputStream(), true).println("YOU");
                        }
                        for (PlayerSocket p : playerSockets) {
                            p.playing = false;
                        }
                    }

                }

                try {
                    Thread.sleep(2);
                } catch (InterruptedException ex) {
                    Logger.getLogger(C4GameServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            System.out.println("Exception in server loop: " + ex);
            Logger.getLogger(C4GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class PlayerSocket extends Thread {

        private final Socket socket;
        private final ConcurrentLinkedQueue<String> messages;
        private int playerId;
        private int enemyId;
        private boolean ready = false;
        private boolean playing = false;

        public ConcurrentLinkedQueue<String> getMessages() {
            return messages;
        }

        public PlayerSocket(Socket socket) {
            this.socket = socket;
            messages = new ConcurrentLinkedQueue<String>();
            messages.add("CON");
            System.out.println("Player Connected");
        }

        Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {

            try {

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (!socket.isClosed()) {
                    String s = in.readLine();
                    if (s == null) {
                        break;
                    }

                    System.out.println("Player adding to queue: " + s);

                    if (s.startsWith("SEL")) {
                        playerId = Character.getNumericValue(s.charAt(3));
                        messages.add("DIS" + Character.getNumericValue(s.charAt(3)));
                        ready = true;
                        continue;
                    }

                    if (s.startsWith("PLY")) {
                        playing = true;
                        continue;
                    }

                    if (s.startsWith("RDY")) {
                        ready = true;
                        continue;
                    }

                    messages.add(s);
                }

            } catch (Exception e) {
                Logger.getLogger(C4GameServer.class.getName()).log(Level.SEVERE, null, e);
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {
                    System.out.println("trying to close socket after failover");
                }
            }

        }
    }

//    public static void main(String[] args) {
//        new C4GameServer().start();
//    }
}
