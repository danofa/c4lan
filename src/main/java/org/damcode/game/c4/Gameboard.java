package org.damcode.game.c4;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author dm
 */
public class Gameboard {

    private int[][] board;
    private Sprite[][] spriteBoard;

    int moveCounter;
    boolean gamedone = false;
    boolean piecedropping = false;
    int[] lastdrop = new int[2];
    int dropStep = 0;
    private Player winner;

    public Player getWinner() {
        return winner;
    }

    public Gameboard() {
        board = new int[7][6];
        spriteBoard = new Sprite[7][6];
    }

    public void reset() {
        board = new int[7][6];
        spriteBoard = new Sprite[7][6];

        lastdrop = new int[2];
        moveCounter = 0;
        gamedone = false;
        piecedropping = false;
        dropStep = 0;
    }

    public int dropPiece(Player player, int col) {
        if (gamedone)
            return -1;
        if (canDrop(col)) {
            moveCounter++;
            System.out.println("moves played: " + moveCounter);
            for (int i = 0; i < board[col].length; i++) {
                if (board[col][i] == 0) {
                    board[col][i] = player.getId();
                    spriteBoard[col][i] = player.getSprite();

                    if (checkConnect(player.getId(), col, i)) {
                        System.out.println("Player " + player.getId() + " is winner!");
                        System.out.println("Winning line: " + connectLine);
                        winner = player;
                        gamedone = true;
                    }
                    lastdrop[0] = col;
                    lastdrop[1] = i;
                    piecedropping = true;
                    dropStep = 70;
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean canDrop(int col) {
        System.out.println("trying to drop: " + col);
        for (int i = 0; i < board[col].length; i++) {

            if (board[col][i] == 0)
                return true;
        }

        return false;
    }

    public void render(Graphics g, Player player, Player enemy) {

        for (int col = 0; col < board.length; col++) {
            top:
            for (int row = 0; row < board[col].length; row++) {

                int p = board[col][row];

                if (piecedropping && col == lastdrop[0] && row == lastdrop[1]) {
                    if (p != 0) {
                        spriteBoard[col][row].render(g, col * 35, dropStep, false);
                    }
                    continue;
                }

                for (int[] coords : connectLine) {
                    if (gamedone && connectLine.size() >= 4) {
                        if (col == coords[0] && row == coords[1]) {
                            spriteBoard[col][row].render(g, col * 35, 245 - (row * 35), false);
                            Sprite.sparkles.render(g, col * 35, 245 - (row * 35), true);
                            continue top;
                        }
                    }
                }

                if (p != 0) {
                    spriteBoard[col][row].render(g, col * 35, 245 - (row * 35), false);
                }

            }
        }

    }

    public void update() {
        if (moveCounter >= 7 * 6) {
            System.out.println("no possible moves left!");
            gamedone = true;
            winner = null;
        }

        if (piecedropping) {
            int dropend = 245 - (lastdrop[1] * 35);
            if (dropStep <= dropend) {
                dropStep += 5;
            } else {
                piecedropping = false;
            }
        }
    }

    ArrayList<int[]> connectLine = new ArrayList<int[]>();

    public boolean checkConnect(int player, int col, int row) {
        int connected = 0;

        for (int r = 0; r < board[col].length; r++) {
            if (board[col][r] == player) {
                connected++;
                connectLine.add(new int[]{col, r});
                if (connected == 4)
                    return true;
            } else {
                connectLine.removeAll(connectLine);
                connected = 0;
            }
        }

        connected = 0;

        for (int c = 0; c < board.length; c++) {
            if (board[c][row] == player) {
                connected++;
                connectLine.add(new int[]{c, row});
                if (connected == 4)
                    return true;
            } else {
                connectLine.removeAll(connectLine);
                connected = 0;
            }
        }

        connected = 0;

        int[] cr = getStartDiagBL(col, row);
        int stepCol = cr[0];
        int stepRow = cr[1];

        while (stepCol < 7 && stepRow < 6) {
            if (board[stepCol][stepRow] == player) {
                connected++;
                connectLine.add(new int[]{stepCol, stepRow});
                if (connected == 4)
                    return true;
            } else {
                connectLine.removeAll(connectLine);
                connected = 0;
            }
            stepCol++;
            stepRow++;
        }

        connected = 0;

        cr = getStartDiagTL(col, row);
        stepCol = cr[0];
        stepRow = cr[1];

        while (stepCol < 7 && stepRow >= 0) {
            if (board[stepCol][stepRow] == player) {
                connected++;
                connectLine.add(new int[]{stepCol, stepRow});
                if (connected == 4)
                    return true;
            } else {
                connectLine.removeAll(connectLine);
                connected = 0;
            }
            stepCol++;
            stepRow--;
        }

        connected = 0;

        return false;
    }

    private int[] getStartDiagBL(int col, int row) {
        if (col > row)
            return new int[]{col - row, 0};
        if (col < row)
            return new int[]{0, row - col};

        return new int[]{0, 0};
    }

    private int[] getStartDiagTL(int col, int row) {
        while (col > 0 && row < 5) {
            col--;
            row++;
        }

        return new int[]{col, row};
    }

    @Override
    public String toString() {
        String out = "";

        for (int col = 0; col < board.length; col++) {
            for (int row = 0; row < board[col].length; row++) {
                out += " - " + board[col][row];
            }
            out += "\r\n";
        }
        return out;
    }

}
