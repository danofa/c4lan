package org.damcode.game.c4;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author dm
 */
public class C4ChatProcessor {

    Font font = new Font("Arial", Font.PLAIN, 12);
    Font fonti = new Font("Arial", Font.ITALIC, 12);
    private ArrayList<String> messages;

    private static int CHAT_X = 252, CHAT_Y = 82;

    public C4ChatProcessor() {
        messages = new ArrayList<String>();
    }

    public void render(Graphics g) {
        g.setColor(Color.BLACK);

        for (int i = 0; i < messages.size(); i++) {
            String s = messages.get(i);
            if (s.startsWith("<")) {
                g.setFont(fonti);
            } else if (s.startsWith(">")) {
                g.setFont(font);
            }
            g.drawString(messages.get(i), CHAT_X, CHAT_Y + (i * 14));
        }
    }

    public void update() {

    }

    public void addText(String t) {

        if (t.length() < 23) {
            messages.add(t);
        } else {
            String lines[] = wrap(t, 23).split("\n");
            for (String s : lines) {
                messages.add(s);
            }
            messages.add(" ");
        }

        System.out.println("messages count: " + messages.size());

        if (messages.size() >= 14) {
            int count = messages.size() - 14;
            for (int i = 0; i < count; i++) {
                messages.remove(i);
            }
        }
    }

    public static String wrap(String in, int length) {
        //:: Trim
        while (in.length() > 0 && (in.charAt(0) == '\t' || in.charAt(0) == ' ')) {
            in = in.substring(1);
        }

        //:: If Small Enough Already, Return Original
        if (in.length() < length)
            return in;

        //:: If Next length Contains Newline, Split There
        if (in.substring(0, length).contains(newline))
            return in.substring(0, in.indexOf(newline)).trim() + newline
                    + wrap(in.substring(in.indexOf("\n") + 1), length);

        //:: Otherwise, Split Along Nearest Previous Space/Tab/Dash
        int spaceIndex = Math.max(Math.max(in.lastIndexOf(" ", length),
                in.lastIndexOf("\t", length)),
                in.lastIndexOf("-", length));

        //:: If No Nearest Space, Split At length
        if (spaceIndex == -1)
            spaceIndex = length;

        //:: Split
        return in.substring(0, spaceIndex).trim() + newline + wrap(in.substring(spaceIndex), length);
    }
    private static final String newline = System.getProperty("line.separator");
}
