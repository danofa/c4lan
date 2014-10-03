package org.damcode.game.c4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dm
 */
public class GameTcpClient {

    private Socket socket;
    private static final int PORT = 5065;
    private BufferedReader in;

    public BufferedReader getInput() {
        if (socket.isConnected()) {
            try {
                if (in == null) {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                return in;
            } catch (IOException ex) {
                Logger.getLogger(GameTcpClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public void sendString(String s) {
        getOutput().println(s);
    }

    public String readString() throws IOException {
        return getInput().readLine();
    }

    public PrintWriter getOutput() {
        if (socket.isConnected()) {
            try {
                return new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException ex) {
                Logger.getLogger(GameTcpClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public GameTcpClient(String address) {
        try {
            socket = new Socket(address, PORT);
        } catch (IOException ex) {
            System.out.println("trying to connect to server failed" + ex);
        }
        if (socket.isConnected()) {
            System.out.println("Connected to server!");
        }
    }

    public GameTcpClient() {
        this("");
    }

    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public Socket getSocket() {
        if (socket != null) {
            return socket;
        }
        return null;
    }

    public void shutdown() {
        if (socket.isClosed()) {
            System.out.println("cant close client socket, already closed");
        } else {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("failed to close client socket" + ex);
            }
            System.out.println("closed client socket");
        }
    }

}
