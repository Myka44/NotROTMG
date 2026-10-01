package com.notrotmg.domain;

import java.util.Objects;

public abstract class Entity {
    private final String id;
    private final double size;
    private final EntityStats stats;
    private Position position;
    private int currentHealth;

    protected Entity(String id, Position position, double size, EntityStats stats) {
        this.id = Objects.requireNonNull(id);
        this.position = Objects.requireNonNull(position);
        this.stats = Objects.requireNonNull(stats);
        if (size <= 0) {
            throw new IllegalArgumentException("Entity size must be positive");
        }
        this.size = size;
        this.currentHealth = stats.maxHealth();
    }

    public final String id() {
        return id;
    }

    public final Position position() {
        return position;
    }

    protected final void position(Position position) {
        this.position = Objects.requireNonNull(position);
    }

    public final double size() {
        return size;
    }

    public final EntityStats stats() {
        return stats;
    }

    public final int currentHealth() {
        return currentHealth;
    }
}
