package com.notrotmg.domain;

public record Position(double x, double y) {
    public Position translated(Vector2 direction, double distance) {
        return new Position(x + direction.x() * distance, y + direction.y() * distance);
    }
}
