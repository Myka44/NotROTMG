package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.ProjectileDefinition;
import com.notrotmg.domain.projectile.RadialShotStrategy;

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
    private static final ProjectileDefinition PROJECTILE =
            new ProjectileDefinition("guardian-projectile", 35, 300, 14, 2);
    private static final AttackStrategy ATTACK_STRATEGY = new RadialShotStrategy(8);

    public BossEnemy(String id, Position position) {
        super(
                id,
                "Ancient Guardian",
                EnemyType.BOSS,
                position,
                SIZE,
                STATS,
                PROJECTILE,
                ATTACK_STRATEGY
        );
    }

    private BossEnemy(
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
                EnemyType.BOSS,
                position,
                size,
                stats,
                projectileDefinition,
                attackStrategy
        );
    }

    @Override
    public Enemy copy(String id, Position position) {
        return new BossEnemy(
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
