package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.enemy.Enemy;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;

import java.util.ArrayList;
import java.util.List;

public final class RegularEnemySpeedDecorator extends RegularEnemyDecorator {
    private static final double EXTRA_SPEED = 40;

    public RegularEnemySpeedDecorator(Enemy wrapped) {
        super(wrapped);
    }

    @Override
    public EntityStats stats() {
        EntityStats stats = wrapped.stats();
        return new EntityStats(
                stats.maxHealth(),
                stats.maxMana(),
                stats.damage(),
                stats.defense(),
                stats.speed() + EXTRA_SPEED,
                stats.spellStrength()
        );
    }

    @Override
    public List<RegularEnemyAccessory> accessories() {
        List<RegularEnemyAccessory> accessories = new ArrayList<>(wrapped.accessories());
        if (!accessories.contains(RegularEnemyAccessory.BOOTS)) {
            accessories.add(RegularEnemyAccessory.BOOTS);
        }
        return List.copyOf(accessories);
    }
}