package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;

public final class RegularEnemy extends Enemy {
    private static final double SIZE = 28;
    private static final EntityStats STATS = new EntityStats(60, 0, 8, 2, 120, 0);

    public RegularEnemy(String id, Position position) {
        super(id, "Slime", EnemyType.REGULAR, position, SIZE, STATS);
    }
}
