package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

import java.util.Objects;

public final class Projectile {
    private final String id;
    private final String ownerId;
    private final String spriteId;
    private final Vector2 trajectory;
    private final double speed;
    private final double length;
    private final double damage;
    private final double lifetimeSeconds;
    private Position position;
    private double remainingLifetimeSeconds;

    Projectile(
            String id,
            String ownerId,
            String spriteId,
            Position position,
            Vector2 trajectory,
            double speed,
            double length,
            double damage,
            double lifetimeSeconds
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Projectile ID cannot be blank");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Projectile owner ID cannot be blank");
        }
        if (spriteId == null || spriteId.isBlank()) {
            throw new IllegalArgumentException("Projectile sprite ID cannot be blank");
        }
        if (speed < 0 || length < 0 || damage < 0) {
            throw new IllegalArgumentException("Projectile statistics cannot be negative");
        }
        if (lifetimeSeconds <= 0) {
            throw new IllegalArgumentException("Projectile lifetime must be positive");
        }
        this.id = id;
        this.ownerId = ownerId;
        this.spriteId = spriteId;
        this.position = Objects.requireNonNull(position);
        this.trajectory = Objects.requireNonNull(trajectory).normalized();
        if (this.trajectory.equals(Vector2.ZERO)) {
            throw new IllegalArgumentException("Projectile trajectory cannot be zero");
        }
        this.speed = speed;
        this.length = length;
        this.damage = damage;
        this.lifetimeSeconds = lifetimeSeconds;
        this.remainingLifetimeSeconds = lifetimeSeconds;
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        position = position.translated(trajectory, speed * deltaSeconds);
        remainingLifetimeSeconds = Math.max(0, remainingLifetimeSeconds - deltaSeconds);
    }

    public boolean isExpired() {
        return remainingLifetimeSeconds <= 0;
    }

    public String id() {
        return id;
    }

    public String ownerId() {
        return ownerId;
    }

    public String spriteId() {
        return spriteId;
    }

    public Position position() {
        return position;
    }

    public Vector2 trajectory() {
        return trajectory;
    }

    public double speed() {
        return speed;
    }

    public double length() {
        return length;
    }

    public double damage() {
        return damage;
    }

    public double lifetimeSeconds() {
        return lifetimeSeconds;
    }

    public double remainingLifetimeSeconds() {
        return remainingLifetimeSeconds;
    }
}
