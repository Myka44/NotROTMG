package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.KineticProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.SingleShotStrategy;

import java.util.function.Supplier;

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
    private static final ProjectileSpec PROJECTILE =
            new ProjectileSpec("slime-projectile", 8, 260, 10, 1.6);
    private static final AttackStrategy ATTACK_STRATEGY = new SingleShotStrategy();
    private static final Supplier<ProjectileBuilder> PROJECTILE_BUILDER =
            () -> new KineticProjectileBuilder(4);

    public RegularEnemy(String id, Position position) {
        super(
                id,
                "Slime",
                EnemyType.REGULAR,
                position,
                SIZE,
                STATS,
                PROJECTILE,
                ATTACK_STRATEGY,
                PROJECTILE_BUILDER
        );
    }

    private RegularEnemy(
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
                EnemyType.REGULAR,
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
        return new RegularEnemy(
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
