package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

import java.util.Objects;
import java.util.UUID;

public final class MagicProjectileBuilder implements ProjectileBuilder {
    private static final double ELEMENTAL_DAMAGE_MULTIPLIER = 1.25;

    private final String element;
    private Position position;
    private Vector2 trajectory;
    private String ownerId;
    private ProjectileSpec spec;

    public MagicProjectileBuilder(String element) {
        if (element == null || element.isBlank()) {
            throw new IllegalArgumentException("A magic projectile requires an element");
        }
        this.element = element;
    }

    @Override
    public void setMovement(Position position, Vector2 trajectory) {
        this.position = position;
        this.trajectory = trajectory;
    }

    @Override
    public void setOwner(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public void setStats(ProjectileSpec spec) {
        this.spec = spec;
    }

    public String element() {
        return element;
    }

    @Override
    public Projectile build() {
        ProjectileSpec stats = Objects.requireNonNull(spec, "Projectile stats must be set before building");
        return new Projectile(
                UUID.randomUUID().toString(),
                ownerId,
                stats.spriteId(),
                position,
                trajectory,
                stats.speed(),
                stats.length(),
                stats.damage() * ELEMENTAL_DAMAGE_MULTIPLIER,
                stats.lifetimeSeconds()
        );
    }
}
