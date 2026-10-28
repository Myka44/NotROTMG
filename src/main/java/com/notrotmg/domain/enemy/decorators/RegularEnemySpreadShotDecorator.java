package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.enemy.RegularEnemy;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import com.notrotmg.domain.projectile.SpreadShotStrategy;

public final class RegularEnemySpreadShotDecorator extends RegularEnemyDecorator {
    private static final int PROJECTILE_COUNT = 3;
    private static final double ANGLE_STEP_DEGREES = 15;

    public RegularEnemySpreadShotDecorator(RegularEnemy wrapped) {
        super(wrapped);
    }

    @Override
    protected RegularEnemy decorate(RegularEnemy enemy) {
        return enemy.withAttackStrategy(new SpreadShotStrategy(
                PROJECTILE_COUNT,
                ANGLE_STEP_DEGREES
        )).withAccessory(RegularEnemyAccessory.SWORD);
    }
}