package org.damcode.game.c4;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author dm
 */
public class C4GameScreen extends Canvas implements Runnable {

    static final int CANVAS_WIDTH = 400;
    static final int CANVAS_HEIGHT = 300;
    private boolean running = true, playing = false;

    BufferedImage buffer = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
    Image gameScreen;
    Player player, enemy;
    public C4MouseHandler mouse;
    public int locX, locY = 35;
    int animframe = 0;
    public Gameboard gameBoard;
    GameTcpClient client;
    JFrame f, starter;
    String keyboardString = "";
    CyclicIntList bgColors;
    C4ChatProcessor chat;

    public void init() {
        chat = new C4ChatProcessor();
        mouse = new C4MouseHandler();
        addMouseMotionListener(mouse);
        addMouseListener(mouse);
        final int MAX_MESSAGE_LENGTH = 55;

        addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_BACK_SPACE || key == KeyEvent.VK_DELETE) && keyboardString.length() > 0) {
                    keyboardString = keyboardString.substring(0, keyboardString.length() - 1);
                } else if (key <= 90 && key >= 40 || key == KeyEvent.VK_SPACE || key == KeyEvent.VK_EXCLAMATION_MARK) {
                    if (keyboardString.length() > MAX_MESSAGE_LENGTH)
                        return;
                    keyboardString += e.getKeyChar();
                } else if (key == KeyEvent.VK_ENTER) {
                    client.sendString("CHT" + keyboardString);
                    chat.addText("> " + keyboardString);
                    keyboardString = "";
                } else if (key == KeyEvent.VK_ESCAPE){
                    gameBoard.reset();
                    client.sendString("RDY");
                } else 
                    System.out.println("keycode: " + key);

            }

            public void keyReleased(KeyEvent e) {

            }
        });

        bgColors = new CyclicIntList();
        bgColors.add(0x0094FF);
        bgColors.add(0x606060);
        bgColors.add(0xA5D5FF);

        gameBoard = new Gameboard();
        try {
            gameScreen = ImageIO.read(getClass().getResource("/gamescreen.png"));
        } catch (IOException ex) {
            Logger.getLogger(C4GameScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public C4GameScreen(Player player, Player enemy, GameTcpClient client) {
        this.enemy = enemy;
        this.player = player;
        this.client = client;
    }

    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = buffer.getGraphics();

        g.setColor(new Color(bgColors.get()));

        g.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        locX = mouse.x - player.getSprite().image.getWidth(null) / 2;

        locX = locX / 35 * 35;
        if (locX > 35 * 6)
            locX = 35 * 6;

        if (mouse.bombclick) {
            g.drawImage(Sprite.bombselected.image, locX, locY, this);
            g.drawImage(Sprite.bombselected.image, 275, 25, null);
        } else {
            g.drawImage(Sprite.bomb.image, 275, 25, null);
            g.drawImage(player.getSprite().image, locX, locY, this);
        }

        gameBoard.render(g, player, enemy);
        g.drawImage(gameScreen, 0, 0, null);

        chat.render(g);

        g.setColor(Color.black);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        if (player.hasTurn() && playing)
            g.drawString("Your move", 15, 20);
        else
            g.drawString("Waiting..", 15, 20);

        if (playing)
            g.drawString("w: " + player.getWins() + " / " + player.getLosses(), 140, 20);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString(">" + keyboardString, 8, 295);

        g = bs.getDrawGraphics();
        g.drawImage(buffer, 0, 0, null);
        g.dispose();
        bs.show();

    }

    @Override
    public void run() {

        f = new JFrame("C4 Dam");
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.add(this);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

        init();

        new Thread() {
            @Override
            public void run() {
                doReads();
            }
        }.start();

        int ups = 0, fps = 0;
        double ns = 1000000000.0 / 60;
        long last = System.nanoTime();
        long ft = System.currentTimeMillis();
        double delta = 0.0;

        client.sendString("PLY");
        client.sendString("RDY");
        requestFocus();
        while (running) {

            long now = System.nanoTime();
            delta += (now - last) / ns;
            last = now;
            while (delta >= 1) {
                delta--;
                update();
                ups++;
            }

            {
                fps++;
                render();
            }

            if (System.currentTimeMillis() - ft > 1000) {
                ft += 1000;
                //System.out.println("fps: " + fps + ", ups: " + ups);
                fps = 0;
                ups = 0;
                focusflash();
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(C4GameScreen.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        stop();
    }

    private void update() {

        player.getSprite().update();
        enemy.getSprite().update();
        Sprite.sparkles.update();

        if (gameBoard.gamedone && playing && !gameBoard.piecedropping) {
            playing = false;
            if (gameBoard.getWinner() == player) {
                player.addWin();
                enemy.addLoss();
                
                player.setHasTurn(true);
                //JOptionPane.showMessageDialog(f, "YOU WON !");
                //gameBoard.reset();
                System.out.println("WIN");
                
            } else if (gameBoard.getWinner() == enemy) {
                player.addLoss();
                enemy.addWin();
                
                //JOptionPane.showMessageDialog(f, "YOU LOST !");
                //gameBoard.reset();
                System.out.println("loss");
                player.setHasTurn(false);
            } else {
                client.sendString("NOMOVS");
                JOptionPane.showMessageDialog(f, "YOU BOTH LOST !");
                gameBoard.reset();
            }
            //client.sendString("RDY");
        }

        gameBoard.update();

        if (mouse.leftClick > 0) {
            mouse.leftClick--;

            if (mouse.x > 275 && mouse.x < 305 && mouse.y > 25 && mouse.y < 55) {
                if (mouse.bombclick) {
                    mouse.bombclick = false;
                } else {
                    mouse.bombclick = true;
                }
            }

            if (player.hasTurn() && playing && mouse.x > 0 && mouse.x < 250) {

                int drop = gameBoard.dropPiece(player, locX / 35);
                if (drop != -1) {
                    client.sendString("MOV" + (locX / 35));
                    client.sendString("YOU");
                    player.setHasTurn(false);
                }
            }
        }
        if (mouse.rightClick > 0) {
            mouse.rightClick--;
            bgColors.next();
        }

        animframe++;
        if (animframe % 30 == 0) {
            if (locY == 35)
                locY = 40;
            else
                locY = 35;
        }
        if (animframe > 999999)
            animframe = 0;
    }

    public void stop() {
        running = false;
    }

    public void start() {
        new Thread(this).start();
    }

    void doReads() {
        BufferedReader in = null;

        while (running) {
            if (client.isConnected()) {
                if (in == null) {
                    in = client.getInput();
                    System.out.println("got input stream");
                }

                String s = "";

                try {
                    s = in.readLine();
                } catch (IOException ex) {

                    JOptionPane.showMessageDialog(null, "Connection Lost!", "Error!", JOptionPane.ERROR_MESSAGE);
                    System.out.println("readline error!" + ex);
                    Logger.getLogger(C4GameScreen.class.getName()).log(Level.SEVERE, null, ex);
                    ((C4StartScreen) starter).setVisible(true);
                    ((C4StartScreen) starter).doCancel();
                    running = false;
                    f.dispose();

                }

                if (s.startsWith("NOMOVS")) {
                    System.out.println("NOMOVS sent");
                }

                if (s.startsWith("RST")) {
                    gameBoard.reset();
                }
                if (s.startsWith("RDY")) {
                    playing = true;
                }

                if (s.startsWith("CHT")) {
                    chat.addText("< " + s.substring(3));
                }

                if (s.startsWith("DCD")) {
                    JOptionPane.showMessageDialog(null, "Player disconnected, closing game..", "Error!", JOptionPane.ERROR_MESSAGE);
                    starter.setVisible(true);
                    ((C4StartScreen) starter).doCancel();
                    running = false;
                    f.dispose();
                }
                if (s.startsWith("YOU")) {
                    System.out.println("got YOU");
                   player.setHasTurn(true);
                }

                if (s.startsWith("MOV")) {
                    gameBoard.dropPiece(enemy, Character.getNumericValue(s.charAt(3)));
                }
            }
        }

    }

    void setStarter(C4StartScreen starter) {
        this.starter = starter;
    }

    boolean ff = true;

    private void focusflash() {
        if (f.isFocused())
            f.setTitle("C4 Dam");

        if (player.hasTurn() && !f.isFocused()) {
            //requestFocus();
            if (ff) {
                f.setTitle("Your Turn!");
                ff = false;
            } else {
                f.setTitle("Alert!");
                ff = true;
            }

        }
    }

}
