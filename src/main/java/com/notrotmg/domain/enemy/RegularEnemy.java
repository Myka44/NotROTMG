package com.notrotmg.domain.enemy;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.KineticProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.SingleShotStrategy;

import java.util.ArrayList;
import java.util.List;
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
    private static final double STOP_DISTANCE = 110;
    private static final double ATTACK_RANGE = 240;
    private static final double ATTACK_COOLDOWN_SECONDS = 1.2;
        private final List<RegularEnemyAccessory> accessories;

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
                PROJECTILE_BUILDER,
                STOP_DISTANCE,
                ATTACK_RANGE,
                ATTACK_COOLDOWN_SECONDS
        );
        this.accessories = List.of();
    }

    public RegularEnemy withStats(EntityStats stats) {
        return new RegularEnemy(
                id(),
                position(),
                name(),
                size(),
                stats,
                projectileSpec(),
                attackStrategy(),
                projectileBuilderFactory(),
                stopDistance(),
                attackRange(),
                attackCooldownSeconds(),
                accessories
        );
    }

    public RegularEnemy withAccessory(RegularEnemyAccessory accessory) {
        List<RegularEnemyAccessory> updatedAccessories = new ArrayList<>(accessories);
        if (!updatedAccessories.contains(accessory)) {
            updatedAccessories.add(accessory);
        }
        return new RegularEnemy(
                id(),
                position(),
                name(),
                size(),
                stats(),
                projectileSpec(),
                attackStrategy(),
                projectileBuilderFactory(),
                stopDistance(),
                attackRange(),
                attackCooldownSeconds(),
                updatedAccessories
        );
    }

    public RegularEnemy withAttackStrategy(AttackStrategy attackStrategy) {
        return new RegularEnemy(
                id(),
                position(),
                name(),
                size(),
                stats(),
                projectileSpec(),
                attackStrategy,
                projectileBuilderFactory(),
                stopDistance(),
                attackRange(),
                                attackCooldownSeconds(),
                                accessories
        );
    }

        public List<RegularEnemyAccessory> accessories() {
                return accessories;
        }

    private RegularEnemy(
            String id,
            Position position,
            String name,
            double size,
            EntityStats stats,
            ProjectileSpec projectileSpec,
            AttackStrategy attackStrategy,
            Supplier<ProjectileBuilder> projectileBuilderFactory,
            double stopDistance,
            double attackRange,
            double attackCooldownSeconds,
            List<RegularEnemyAccessory> accessories
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
                projectileBuilderFactory,
                stopDistance,
                attackRange,
                attackCooldownSeconds
        );
        this.accessories = List.copyOf(accessories);
    }

    @Override
    public RegularEnemy copy(String id, Position position) {
        return new RegularEnemy(
                id,
                position,
                name(),
                size(),
                stats(),
                projectileSpec(),
                attackStrategy(),
                projectileBuilderFactory(),
                stopDistance(),
                attackRange(),
                attackCooldownSeconds(),
                accessories
        );
    }
}
