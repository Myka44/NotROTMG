package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Entity;
import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;

import java.util.Objects;

public abstract class Enemy extends Entity implements EnemyPrototype {
    private final String name;
    private final EnemyType type;

    protected Enemy(
            String id,
            String name,
            EnemyType type,
            Position position,
            double size,
            EntityStats stats
    ) {
        super(id, position, size, stats);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Enemy name cannot be blank");
        }
        this.name = name;
        this.type = Objects.requireNonNull(type);
    }

    public final String name() {
        return name;
    }

    public final EnemyType type() {
        return type;
    }

    @Override
    public abstract Enemy copy(String id, Position position);
}
