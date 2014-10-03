
package org.damcode.game.c4;

/**
 *
 * @author dm
 */
public class Player {
    
    private int wins;
    private int losses;
    private boolean hasTurn;
    private Sprite sprite;
    private int id;

    public Player(Sprite sprite, int id) {
        this.sprite = sprite;
        this.id = id;
    }

    public int getWins() {
        return wins;
    }

    public void addWin() {
        this.wins++;
    }

    public int getLosses() {
        return losses;
    }

    public void addLoss() {
        this.losses++;
    }

    public boolean hasTurn() {
        return hasTurn;
    }

    public void setHasTurn(boolean hasTurn) {
        this.hasTurn = hasTurn;
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public int getId() {
        return id;
    }
}
