package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import com.notrotmg.domain.enemy.RegularEnemy;

public final class RegularEnemyHealthDecorator extends RegularEnemyDecorator {
    private static final int EXTRA_HEALTH = 40;
    private static final int EXTRA_DEFENSE = 4;

    public RegularEnemyHealthDecorator(RegularEnemy wrapped) {
        super(wrapped);
    }

    @Override
    protected RegularEnemy decorate(RegularEnemy enemy) {
        EntityStats stats = enemy.stats();
        return enemy.withStats(new EntityStats(
                stats.maxHealth() + EXTRA_HEALTH,
                stats.maxMana(),
                stats.damage(),
            stats.defense() + EXTRA_DEFENSE,
                stats.speed(),
                stats.spellStrength()
        )).withAccessory(RegularEnemyAccessory.ARMOR);
    }
}