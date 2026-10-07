package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;

public final class RegularEnemy extends Enemy {
    private static final double SIZE = 28;
    private static final EntityStats STATS = EntityStats.builder()
            .maxHealth(60)
            .maxMana(0)
            .damage(8)
            .defense(2)
            .speed(120)
            .spellStrength(0)
            .build();

    public RegularEnemy(String id, Position position) {
        super(id, "Slime", EnemyType.REGULAR, position, SIZE, STATS);
    }

    private RegularEnemy(String id, Position position, String name, double size, EntityStats stats) {
        super(id, name, EnemyType.REGULAR, position, size, stats);
    }

    @Override
    public Enemy copy(String id, Position position) {
        return new RegularEnemy(id, position, name(), size(), stats());
    }
}
