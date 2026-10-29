package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.MagicProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.RadialShotStrategy;

import java.util.function.Supplier;

public final class BossEnemy extends Enemy implements IPrototype<BossEnemy> {
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
    private static final double STOP_DISTANCE = 160;
    private static final double ATTACK_RANGE = 300;
    private static final double ATTACK_COOLDOWN_SECONDS = 2;

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
                PROJECTILE_BUILDER,
                STOP_DISTANCE,
                ATTACK_RANGE,
                ATTACK_COOLDOWN_SECONDS
        );
    }

        @Override
        public BossEnemy copy() {
                return new BossEnemy(id(), position());
        }

}
