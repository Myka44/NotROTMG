package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;

public final class BossEnemy extends Enemy {
    private static final double SIZE = 64;
    private static final EntityStats STATS = EntityStats.builder()
            .maxHealth(500)
            .maxMana(100)
            .damage(35)
            .defense(18)
            .speed(90)
            .spellStrength(20)
            .build();

    public BossEnemy(String id, Position position) {
        super(id, "Ancient Guardian", EnemyType.BOSS, position, SIZE, STATS);
    }

    private BossEnemy(String id, Position position, String name, double size, EntityStats stats) {
        super(id, name, EnemyType.BOSS, position, size, stats);
    }

    @Override
    public Enemy copy(String id, Position position) {
        return new BossEnemy(id, position, name(), size(), stats());
    }
}
