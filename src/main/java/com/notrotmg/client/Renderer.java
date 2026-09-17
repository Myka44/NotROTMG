package com.notrotmg.client;

import java.awt.*;

public class Renderer {
    public void drawPlayer(Graphics2D g, int x, int y, int size, Color color, String name) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
        g.setColor(Color.WHITE);
        g.drawString(name, x, y - 5);
    }

    public void drawEnemy(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fillOval(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, size, size);
    }

    public void drawMapElement(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x, y, size, size);
    }
}
