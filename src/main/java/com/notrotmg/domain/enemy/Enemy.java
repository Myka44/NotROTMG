package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Entity;
import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.projectile.ActiveProjectile;
import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.ProjectileDefinition;

import java.util.List;
import java.util.Objects;

public abstract class Enemy extends Entity implements EnemyPrototype {
    private final String name;
    private final EnemyType type;
    private final ProjectileDefinition projectileDefinition;
    private final AttackStrategy attackStrategy;

    protected Enemy(
            String id,
            String name,
            EnemyType type,
            Position position,
            double size,
            EntityStats stats,
            ProjectileDefinition projectileDefinition,
            AttackStrategy attackStrategy
    ) {
        super(id, position, size, stats);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enemy name cannot be blank");
        }
        this.name = name;
        this.type = Objects.requireNonNull(type);
        this.projectileDefinition = Objects.requireNonNull(projectileDefinition);
        this.attackStrategy = Objects.requireNonNull(attackStrategy);
    }

    public final String name() {
        return name;
    }

    public final EnemyType type() {
        return type;
    }

    public final List<ActiveProjectile> attack(Vector2 aimDirection) {
        AttackContext context = new AttackContext(id(), position(), aimDirection);
        return attackStrategy.attack(context, projectileDefinition);
    }

    public final ProjectileDefinition projectileDefinition() {
        return projectileDefinition;
    }

    public final AttackStrategy attackStrategy() {
        return attackStrategy;
    }

    @Override
    public abstract Enemy copy(String id, Position position);
}
