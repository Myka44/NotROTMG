package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;

import java.util.Map;
import java.util.Objects;

/** Simple Factory that selects a configured prototype and creates a fresh enemy from it. */
public final class EnemyFactory {
    private static final Position PROTOTYPE_POSITION = new Position(0, 0);
    private static final Map<EnemyType, EnemyPrototype> PROTOTYPES = Map.of(
            EnemyType.REGULAR, new RegularEnemy("regular-prototype", PROTOTYPE_POSITION),
            EnemyType.BOSS, new BossEnemy("boss-prototype", PROTOTYPE_POSITION)
    );

    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        return PROTOTYPES.get(type).copy(id, position);
    }
}
