package com.notrotmg.domain;

public record Vector2(double x, double y) {
    public static final Vector2 ZERO = new Vector2(0, 0);

    public Vector2 normalized() {
        double length = Math.hypot(x, y);
        return length == 0 ? ZERO : new Vector2(x / length, y / length);
    }
}
