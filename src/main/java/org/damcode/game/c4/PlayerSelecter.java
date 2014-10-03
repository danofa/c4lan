package org.damcode.game.c4;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author dm
 */
public class PlayerSelecter extends JPanel {

    ButtonGroup radioGroup;
    private Sprite selectedSprite, disabledSprite;

    public PlayerSelecter() {
        radioGroup = new ButtonGroup();
        selectedSprite = null;
        setLayout(new FlowLayout());
        for (int i = 0; i < Sprite.sprites.length; i++) {
            JRadioButton rbtn = new DamC4RadioButton(Sprite.sprites[i]);
            radioGroup.add(rbtn);
            add(rbtn);
        }
        setVisible(true);
    }

    @Override
    public void setEnabled(boolean enabled) {
        Component[] components = getComponents();
        for (Component c : components) {
            DamC4RadioButton btn = (DamC4RadioButton) c;
            btn.setEnabled(enabled);
            if (!enabled)
                radioGroup.clearSelection();
        }
    }

    public Sprite getSelectedSprite() {
        System.out.println("get selected sprite!");
        return selectedSprite;
    }

    public void disableSprite(int id) {
        Component[] components = getComponents();
        for (Component c : components) {
            DamC4RadioButton btn = (DamC4RadioButton) c;
            if (btn.sprite.id == id) {
                c.setEnabled(false);
            }
        }

        System.out.println("got disable : " + id);
        disabledSprite = Sprite.getSpriteFromId(id);
    }

    public Sprite getDisabledSprite() {
        return disabledSprite;
    }

    class DamC4RadioButton extends JRadioButton {

        final Sprite sprite;

        public DamC4RadioButton(Sprite sprite) {
            super(new ImageIcon(sprite.image));
            this.sprite = sprite;
            this.setBorderPainted(true);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("EDT" + SwingUtilities.isEventDispatchThread());
                    selectedSprite = getSprite();
                }
            });
        }

        public Sprite getSprite() {
            System.out.println("sprite selected: " + this.sprite.id);
            return this.sprite;
        }
    }

    public static void main(String[] args) {

        JFrame f = new JFrame("C4");
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PlayerSelecter ps = new PlayerSelecter();
        f.add(ps);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

}
