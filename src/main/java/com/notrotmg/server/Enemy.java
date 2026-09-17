package com.notrotmg.server;

public class Enemy {
    public final int id;
    public final String name;
    public int x;
    public int y;
    public final int type;
    public int hp;

    public Enemy(int id, String name, int x, int y, int type, int hp) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
        this.hp = hp;
    }
}
