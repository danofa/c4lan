package org.damcode.game.c4;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author dm
 */
public class C4StartScreen extends JFrame {

    JButton btStart, btJoin, btCancel, btGo;
    JLabel lbInfo;
    JPanel panel;

    boolean hosting, clienting, cango = false;
    GameTcpClient client;
    static C4GameServer gameServer;
    PlayerSelecter selecter;
    Player player, enemy;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new C4StartScreen().init();
            }
        });
    }

    public void init() {
        btStart = new JButton(new ImageIcon(getClass().getResource("/start.png")));
        btJoin = new JButton(new ImageIcon(getClass().getResource("/join.png")));
        btStart.setSize(btStart.getIcon().getIconWidth(), btStart.getIcon().getIconHeight());
        btJoin.setSize(btJoin.getIcon().getIconWidth(), btJoin.getIcon().getIconHeight());
        btCancel = new JButton("Cancel");
        btGo = new JButton("Go !");

        btCancel.addActionListener(new CancelAction());
        btGo.addActionListener(new GoAction());
        btStart.addActionListener(new StartAction());
        btJoin.addActionListener(new JoinAction());
        
        

        lbInfo = new JLabel("  ");
        setLayout(new BorderLayout());
        add(btStart, BorderLayout.NORTH);
        add(btJoin, BorderLayout.CENTER);

        selecter = new PlayerSelecter();

        Component[] components = selecter.getComponents();
        for (Component c : components) {
            ((PlayerSelecter.DamC4RadioButton) c).addActionListener(new spriteSelectListener());
        }

        JPanel temp = new JPanel(new BorderLayout());
        JPanel temp2 = new JPanel(new FlowLayout());

        temp2.add(btCancel);
        temp2.add(btGo);
        temp.add(temp2, BorderLayout.NORTH);
        temp.add(lbInfo, BorderLayout.SOUTH);
        temp.add(selecter, BorderLayout.CENTER);
        selecter.setEnabled(false);
        btGo.setEnabled(false);

        add(temp, BorderLayout.SOUTH);

        try {
            setIconImage(ImageIO.read(getClass().getResource("/icon.ico")));
        } catch (IOException ex) {
            Logger.getLogger(C4StartScreen.class.getName()).log(Level.SEVERE, null, ex);
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setTitle("Start the game!");
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void mainLoop() {

        while (hosting || clienting) {
            try {

                String s = client.readString();

                if (s.startsWith("DIS")) {
                    if (clienting) {
                        selecter.setEnabled(true);
                    }
                    selecter.disableSprite(Character.getNumericValue(s.charAt(3)));
                    lbInfo.setText("Select image...");
                }

                if (s.startsWith("CON")) {
                    if (hosting) {
                        System.out.println("Got CON in mainLoop()");
                        lbInfo.setText("Player connected");
                    }
                }

                if (s.startsWith("RDY")) {
                    btGo.setEnabled(true);
                    lbInfo.setText("Ready to play, click Go!");
                    selecter.setEnabled(false);
                    break;
                }

            } catch (IOException ex) {
                System.out.println("mainLoop socket input error: " + ex);
                break;
            }
        }
    }

    class StartAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (hosting)
                return;

            gameServer = new C4GameServer();
            gameServer.start();
            hosting = true;
            btJoin.setEnabled(false);

            lbInfo.setText("Starting game...");
            client = new GameTcpClient();

            if (client.isConnected()) {
                new Thread() {
                    @Override
                    public void run() {
                        mainLoop();
                    }
                }.start();
            }
            selecter.setEnabled(true);
            lbInfo.setText("Select image...");
        }
    }

    public void sendMessage(String m) {
        if (hosting || clienting && client != null) {
            client.getOutput().println(m);
        }
    }

    class spriteSelectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage("SEL" + ((PlayerSelecter.DamC4RadioButton) e.getSource()).sprite.id);
            if (hosting) {
                lbInfo.setText("Waiting on other player");
            }
        }
    }

    GameFinderClient finder;

    class JoinAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (clienting)
                return;

            clienting = true;
            btStart.setEnabled(false);
            finder = new GameFinderClient();
            finder.start();

            new Thread() {
                @Override
                public void run() {
                    while (finder.isWaiting() && clienting) {
                        try {
                            Thread.sleep(1000);
                            lbInfo.setText("Looking for games");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(C4StartScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (clienting) {
                        client = new GameTcpClient(finder.getServer());
                    } else {
                        lbInfo.setText("Cancelled");
                        return;
                    }
                    if (client.isConnected()) {
                        lbInfo.setText("Connected to: " + finder.getServer());
                        finder.stop(); // stop looking for host;
                        finder = null;
                        lbInfo.setText("Waiting on other player");
                        mainLoop();
                    }
                }
            }.start();
        }
    }

    void doCancel() {
        if (hosting) {
            gameServer.shutdown();
            hosting = false;
            client.shutdown();
            client = null;
        }

        if (clienting) {
            clienting = false;

            if (finder != null) {
                finder.stop();
            }

            if (client != null) {
                client.shutdown();
            }
        }
        btJoin.setEnabled(true);
        btStart.setEnabled(true);
        btGo.setEnabled(false);
        selecter.setEnabled(false);
        lbInfo.setText("Cancelled");
    }

    private void doGo() {

        player = new Player(selecter.getSelectedSprite(), 1);
        enemy = new Player(selecter.getDisabledSprite(), 2);

        Dimension d = new Dimension(C4GameScreen.CANVAS_WIDTH, C4GameScreen.CANVAS_HEIGHT);
        C4GameScreen game = new C4GameScreen(player, enemy, client);
        game.setSize(d);
        game.setPreferredSize(d);
        game.setStarter(this);
        game.start();
        setVisible(false);
    }

    class CancelAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doCancel();
        }
    }

    class GoAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            doGo();
        }
    }

}
