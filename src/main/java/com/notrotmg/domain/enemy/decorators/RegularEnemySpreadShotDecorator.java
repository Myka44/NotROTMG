package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.enemy.Enemy;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import com.notrotmg.domain.projectile.SpreadShotStrategy;

import java.util.ArrayList;
import java.util.List;

public final class RegularEnemySpreadShotDecorator extends RegularEnemyDecorator {
    private static final int PROJECTILE_COUNT = 3;
    private static final double ANGLE_STEP_DEGREES = 15;

    public RegularEnemySpreadShotDecorator(Enemy wrapped) {
        super(wrapped);
    }

    @Override
    public com.notrotmg.domain.projectile.AttackStrategy attackStrategy() {
        return new SpreadShotStrategy(
                PROJECTILE_COUNT,
                ANGLE_STEP_DEGREES
        );
    }

    @Override
    public List<RegularEnemyAccessory> accessories() {
        List<RegularEnemyAccessory> accessories = new ArrayList<>(wrapped.accessories());
        if (!accessories.contains(RegularEnemyAccessory.SWORD)) {
            accessories.add(RegularEnemyAccessory.SWORD);
        }
        return List.copyOf(accessories);
    }
}