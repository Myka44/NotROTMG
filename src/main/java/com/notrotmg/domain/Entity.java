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

    public final Position center() {
        return new Position(position.x() + size / 2, position.y() + size / 2);
    }

    public final EntityStats stats() {
        return stats;
    }

    public final int currentHealth() {
        return currentHealth;
    }

    public final boolean isAlive() {
        return currentHealth > 0;
    }

    public final void takeDamage(double damage) {
        if (!Double.isFinite(damage) || damage < 0) {
            throw new IllegalArgumentException("Damage must be finite and non-negative");
        }
        if (!isAlive() || damage == 0) {
            return;
        }
        int effectiveDamage = Math.max(1, (int) Math.round(damage - stats.defense() / 2.0));
        currentHealth = Math.max(0, currentHealth - effectiveDamage);
    }
}
