package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;

public final class BossEnemy extends Enemy {
    private static final double SIZE = 64;
    private static final EntityStats STATS = new EntityStats(500, 100, 35, 18, 90, 20);

    public BossEnemy(String id, Position position) {
        super(id, "Ancient Guardian", EnemyType.BOSS, position, SIZE, STATS);
    }
}
