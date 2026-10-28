package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.enemy.RegularEnemy;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;

public final class RegularEnemySpeedDecorator extends RegularEnemyDecorator {
    private static final double EXTRA_SPEED = 40;

    public RegularEnemySpeedDecorator(RegularEnemy wrapped) {
        super(wrapped);
    }

    @Override
    protected RegularEnemy decorate(RegularEnemy enemy) {
        EntityStats stats = enemy.stats();
        return enemy.withStats(new EntityStats(
                stats.maxHealth(),
                stats.maxMana(),
                stats.damage(),
                stats.defense(),
                stats.speed() + EXTRA_SPEED,
                stats.spellStrength()
        )).withAccessory(RegularEnemyAccessory.BOOTS);
    }
}