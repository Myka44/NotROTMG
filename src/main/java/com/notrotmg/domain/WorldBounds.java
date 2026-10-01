package com.notrotmg.domain;

public record WorldBounds(double width, double height) {
    public WorldBounds {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive");
        }
    }

    public Position clamp(Position position, double entitySize) {
        double x = Math.max(0, Math.min(width - entitySize, position.x()));
        double y = Math.max(0, Math.min(height - entitySize, position.y()));
        return new Position(x, y);
    }
}
