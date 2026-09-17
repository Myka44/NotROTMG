package com.notrotmg.shared;

public interface RendererStrategy {
    void render(java.awt.Graphics2D g, int x, int y, int size, java.awt.Color color);
}
