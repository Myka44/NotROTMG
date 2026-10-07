package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.ProjectileDefinition;
import com.notrotmg.domain.projectile.SingleShotStrategy;

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
    private static final ProjectileDefinition PROJECTILE =
            new ProjectileDefinition("slime-projectile", 8, 260, 10, 1.6);
    private static final AttackStrategy ATTACK_STRATEGY = new SingleShotStrategy();

    public RegularEnemy(String id, Position position) {
        super(
                id,
                "Slime",
                EnemyType.REGULAR,
                position,
                SIZE,
                STATS,
                PROJECTILE,
                ATTACK_STRATEGY
        );
    }

    private RegularEnemy(
            String id,
            Position position,
            String name,
            double size,
            EntityStats stats,
            ProjectileDefinition projectileDefinition,
            AttackStrategy attackStrategy
    ) {
        super(
                id,
                name,
                EnemyType.REGULAR,
                position,
                size,
                stats,
                projectileDefinition,
                attackStrategy
        );
    }

    @Override
    public Enemy copy(String id, Position position) {
        return new RegularEnemy(
                id,
                position,
                name(),
                size(),
                stats(),
                projectileDefinition(),
                attackStrategy()
        );
    }
}
