package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;

import java.util.Objects;

/** Simple Factory that selects and creates one concrete enemy product. */
public final class EnemyFactory {
    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        return switch (type) {
            case REGULAR -> new RegularEnemy(id, position);
            case BOSS -> new BossEnemy(id, position);
        };
    }
}
