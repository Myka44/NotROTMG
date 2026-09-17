package com.notrotmg.server;

public class MapElement {
    public final int id;
    public int x;
    public int y;
    public final int type;

    public MapElement(int id, int x, int y, int type) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
