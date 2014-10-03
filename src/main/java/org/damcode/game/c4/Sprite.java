package org.damcode.game.c4;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author dm
 */
public class Sprite {

    private static final String sheetfile = "/playerpieces.png";
    private static final int NUM_ROWS = 8;
    private static final int NUM_COLS = 8;
    public int w;
    public int h;
    public Image image;
    public int id;
    public int x, y;
    boolean shake = false;
    int[] pixels;

    public static final Sprite apple = new Sprite(0, 0, 1);
    public static final Sprite lemon = new Sprite(1, 0, 2);
    public static final Sprite burger = new Sprite(2, 0, 3);
    public static final Sprite orange = new Sprite(3, 0, 4);
    public static final Sprite cherry = new Sprite(4, 0, 5);
    public static final Sprite lolipop = new Sprite(5, 0, 6);
    public static final Sprite sparkles = new Sprite(0, 2, 0);

    public static final Sprite[] sprites = {cherry, apple, lemon, burger, orange, lolipop};

    public static final Sprite bomb = new Sprite(6, 0, 7);
    public static final Sprite bombselected = new Sprite(6, 2, 8);
    public static final Sprite bombfuse = new Sprite(7, 0, 9);

    public static Sprite getSpriteFromId(int id) {

        for (int i = 0; i < sprites.length; i++) {
            if (sprites[i].id == id)
                return sprites[i];
        }
        return null;
    }

    public Sprite(int col, int row, int id) {
        try {

            image = getImage(col, row);
            this.id = id;

        } catch (IOException ex) {
            Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void render(Graphics g, int x, int y, boolean shake) {
        if (shake) {
            g.drawImage(image, x + x0, y, null);
        } else {
            g.drawImage(image, x, y, null);
        }
    }

    int x0 = -5, xs = 0;

    public void update() {
        xs++;
        System.out.println("xs: " + xs);
        if (xs % 50 == 0)
            x0 = 3;
        else if (xs % 25 == 0)
            x0 = -3;

        if (xs == 999999) {
            xs = 0;
        }
    }

    private Image getImage(int col, int row) throws IOException {

        Image spriteSheet = ImageIO.read(getClass().getResource(sheetfile));
        w = spriteSheet.getWidth(null) / NUM_COLS;
        h = spriteSheet.getHeight(null) / NUM_ROWS;
        Image sprite = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = sprite.getGraphics();
        g.drawImage(spriteSheet, 0, 0, w, h, col * w, row * h, col * w + w, row * h + h, null);

        return sprite;

    }

}
