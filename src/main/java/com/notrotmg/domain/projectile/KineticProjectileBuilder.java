package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

import java.util.Objects;
import java.util.UUID;

public final class KineticProjectileBuilder implements ProjectileBuilder {
    private final double weight;
    private Position position;
    private Vector2 trajectory;
    private String ownerId;
    private ProjectileSpec spec;

    public KineticProjectileBuilder(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("A kinetic projectile requires a positive weight");
        }
        this.weight = weight;
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

    public double weight() {
        return weight;
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
                Math.max(1.0, stats.speed() - weight),
                stats.length(),
                stats.damage() + weight,
                stats.lifetimeSeconds()
        );
    }
}
