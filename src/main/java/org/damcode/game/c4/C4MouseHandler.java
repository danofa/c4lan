package org.damcode.game.c4;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author dm
 */
public class C4MouseHandler implements MouseMotionListener, MouseInputListener {

    public int x, y;
    public int leftClick;
    public int rightClick;
    public int col;
    public boolean bombclick = false;
    
    Cursor blank;
    Image cursor;

    public C4MouseHandler() {

        try {
            cursor = ImageIO.read(getClass().getResource("/cursor.png"));
        } catch (IOException ex) {
            Logger.getLogger(C4MouseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        blank = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

        x = e.getX();
        y = e.getY();

        if (x < 35 * 6 && x > 0) {
            col = x / 35;
        }

        if (x > 0 && x < 250 && y > 0 && y < C4GameScreen.CANVAS_HEIGHT) {
            ((C4GameScreen) e.getSource()).setCursor(blank);
        } else {
            ((C4GameScreen) e.getSource()).setCursor(null);
        }
        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 1)
            leftClick++;
        if (e.getButton() == 3)
            rightClick++;
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
