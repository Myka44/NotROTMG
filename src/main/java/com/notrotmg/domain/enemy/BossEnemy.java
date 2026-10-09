package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.MagicProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.RadialShotStrategy;

import java.util.function.Supplier;

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
    private static final ProjectileSpec PROJECTILE =
            new ProjectileSpec("guardian-projectile", 35, 300, 14, 2);
    private static final AttackStrategy ATTACK_STRATEGY = new RadialShotStrategy(8);
    private static final Supplier<ProjectileBuilder> PROJECTILE_BUILDER =
            () -> new MagicProjectileBuilder("arcane");

    public BossEnemy(String id, Position position) {
        super(
                id,
                "Ancient Guardian",
                EnemyType.BOSS,
                position,
                SIZE,
                STATS,
                PROJECTILE,
                ATTACK_STRATEGY,
                PROJECTILE_BUILDER
        );
    }

    private BossEnemy(
            String id,
            Position position,
            String name,
            double size,
            EntityStats stats,
            ProjectileSpec projectileSpec,
            AttackStrategy attackStrategy,
            Supplier<ProjectileBuilder> projectileBuilderFactory
    ) {
        super(
                id,
                name,
                EnemyType.BOSS,
                position,
                size,
                stats,
                projectileSpec,
                attackStrategy,
                projectileBuilderFactory
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
                projectileSpec(),
                attackStrategy(),
                projectileBuilderFactory()
        );
    }
}
