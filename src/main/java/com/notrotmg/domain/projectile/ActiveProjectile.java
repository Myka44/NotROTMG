package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

import java.util.Objects;
import java.util.UUID;

/** One mutable projectile currently travelling through the game world. */
public final class ActiveProjectile {
    private final String id;
    private final String ownerId;
    private final ProjectileDefinition definition;
    private final Vector2 direction;
    private Position position;
    private double remainingLifetimeSeconds;

    public ActiveProjectile(
            String id,
            String ownerId,
            ProjectileDefinition definition,
            Position position,
            Vector2 direction
    ) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Projectile ID cannot be blank");
        }
        if (ownerId == null || ownerId.isBlank()) {
            throw new IllegalArgumentException("Projectile owner ID cannot be blank");
        }
        this.id = id;
        this.ownerId = ownerId;
        this.definition = Objects.requireNonNull(definition);
        this.position = Objects.requireNonNull(position);
        this.direction = Objects.requireNonNull(direction).normalized();
        if (this.direction.equals(Vector2.ZERO)) {
            throw new IllegalArgumentException("Projectile direction cannot be zero");
        }
        this.remainingLifetimeSeconds = definition.lifetimeSeconds();
    }

    public static ActiveProjectile launch(
            AttackContext context,
            ProjectileDefinition definition,
            Vector2 direction
    ) {
        Objects.requireNonNull(context);
        return new ActiveProjectile(
                UUID.randomUUID().toString(),
                context.attackerId(),
                definition,
                context.origin(),
                direction
        );
    }

    public void update(double deltaSeconds) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        position = position.translated(direction, definition.speed() * deltaSeconds);
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

    public ProjectileDefinition definition() {
        return definition;
    }

    public Position position() {
        return position;
    }

    public Vector2 direction() {
        return direction;
    }

    public double remainingLifetimeSeconds() {
        return remainingLifetimeSeconds;
    }
}
