package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Entity;
import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.WorldBounds;
import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.Projectile;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSide;
import com.notrotmg.domain.projectile.ProjectileSpec;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class Enemy extends Entity {
    private final String name;
    private final EnemyType type;
    private final ProjectileSpec projectileSpec;
    private final AttackStrategy attackStrategy;
    private final Supplier<ProjectileBuilder> projectileBuilderFactory;
    private final double stopDistance;
    private final double attackRange;
    private final double attackCooldownSeconds;
    private double remainingAttackCooldown;

    protected Enemy(
            String id,
            String name,
            EnemyType type,
            Position position,
            double size,
            EntityStats stats,
            ProjectileSpec projectileSpec,
            AttackStrategy attackStrategy,
            Supplier<ProjectileBuilder> projectileBuilderFactory,
            double stopDistance,
            double attackRange,
            double attackCooldownSeconds
    ) {
        super(id, position, size, stats);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enemy name cannot be blank");
        }
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.projectileSpec = Objects.requireNonNull(projectileSpec);
        this.attackStrategy = Objects.requireNonNull(attackStrategy);
        this.projectileBuilderFactory = Objects.requireNonNull(projectileBuilderFactory);
        if (stopDistance < 0 || attackRange < stopDistance || attackCooldownSeconds <= 0) {
            throw new IllegalArgumentException("Invalid enemy movement or attack distances");
        }
        this.stopDistance = stopDistance;
        this.attackRange = attackRange;
        this.attackCooldownSeconds = attackCooldownSeconds;
    }

    public final String name() {
        return name;
    }

    public final EnemyType type() {
        return type;
    }

    public final List<Projectile> attack(Vector2 aimDirection) {
        AttackContext context = new AttackContext(id(), ProjectileSide.ENEMY, center(), aimDirection);
        return attackStrategy.attack(context, projectileSpec, projectileBuilderFactory);
    }

    /** Advances pursuit and fires at a target when it is in range and the cooldown is ready. */
    public final List<Projectile> update(Position targetCenter, double deltaSeconds, WorldBounds bounds) {
        Objects.requireNonNull(targetCenter);
        Objects.requireNonNull(bounds);
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        if (!isAlive()) {
            return List.of();
        }

        remainingAttackCooldown = Math.max(0, remainingAttackCooldown - deltaSeconds);
        Vector2 towardTarget = directionTo(targetCenter);
        double distance = Math.hypot(towardTarget.x(), towardTarget.y());
        if (distance > stopDistance) {
            double step = Math.min(stats().speed() * deltaSeconds, distance - stopDistance);
            position(bounds.clamp(position().translated(towardTarget.normalized(), step), size()));
        }

        towardTarget = directionTo(targetCenter);
        if (remainingAttackCooldown > 0
                || Math.hypot(towardTarget.x(), towardTarget.y()) > attackRange
                || towardTarget.equals(Vector2.ZERO)) {
            return List.of();
        }

        remainingAttackCooldown = attackCooldownSeconds;
        return attack(towardTarget);
    }

    private Vector2 directionTo(Position targetCenter) {
        Position origin = center();
        return new Vector2(targetCenter.x() - origin.x(), targetCenter.y() - origin.y());
    }

    public final ProjectileSpec projectileSpec() {
        return projectileSpec;
    }

    public final AttackStrategy attackStrategy() {
        return attackStrategy;
    }

    public final Supplier<ProjectileBuilder> projectileBuilderFactory() {
        return projectileBuilderFactory;
    }

    public final double stopDistance() {
        return stopDistance;
    }

    public final double attackRange() {
        return attackRange;
    }

    public final double attackCooldownSeconds() {
        return attackCooldownSeconds;
    }

}
