package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Entity;
import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.Projectile;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class Enemy extends Entity implements EnemyPrototype {
    private final String name;
    private final EnemyType type;
    private final ProjectileSpec projectileSpec;
    private final AttackStrategy attackStrategy;
    private final Supplier<ProjectileBuilder> projectileBuilderFactory;

    protected Enemy(
            String id,
            String name,
            EnemyType type,
            Position position,
            double size,
            EntityStats stats,
            ProjectileSpec projectileSpec,
            AttackStrategy attackStrategy,
            Supplier<ProjectileBuilder> projectileBuilderFactory
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
    }

    public final String name() {
        return name;
    }

    public final EnemyType type() {
        return type;
    }

    public final List<Projectile> attack(Vector2 aimDirection) {
        AttackContext context = new AttackContext(id(), position(), aimDirection);
        return attackStrategy.attack(context, projectileSpec, projectileBuilderFactory);
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

    @Override
    public abstract Enemy copy(String id, Position position);
}
