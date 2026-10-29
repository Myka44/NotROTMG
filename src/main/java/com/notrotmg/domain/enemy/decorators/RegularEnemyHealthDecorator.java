package com.notrotmg.domain.enemy.decorators;

import com.notrotmg.domain.EntityStats;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import com.notrotmg.domain.enemy.Enemy;

import java.util.ArrayList;
import java.util.List;

public final class RegularEnemyHealthDecorator extends RegularEnemyDecorator {
    private static final int EXTRA_HEALTH = 40;
    private static final int EXTRA_DEFENSE = 4;
    private int currentHealth;

    public RegularEnemyHealthDecorator(Enemy wrapped) {
        super(wrapped);
        currentHealth = wrapped.currentHealth() + EXTRA_HEALTH;
    }

    @Override
    public RegularEnemyHealthDecorator copy(String id, Position position) {
        return new RegularEnemyHealthDecorator(wrapped.copy(id, position));
    }

    @Override
    public EntityStats stats() {
        EntityStats stats = wrapped.stats();
        return new EntityStats(
                stats.maxHealth() + EXTRA_HEALTH,
                stats.maxMana(),
                stats.damage(),
                stats.defense() + EXTRA_DEFENSE,
                stats.speed(),
                stats.spellStrength()
        );
    }

    @Override
    public int currentHealth() {
        return currentHealth;
    }

    @Override
    public boolean isAlive() {
        return currentHealth > 0;
    }

    @Override
    public void takeDamage(double damage) {
        if (!Double.isFinite(damage) || damage < 0) {
            throw new IllegalArgumentException("Damage must be finite and non-negative");
        }
        if (!isAlive() || damage == 0) {
            return;
        }
        int effectiveDamage = Math.max(1, (int) Math.round(damage - stats().defense() / 2.0));
        currentHealth = Math.max(0, currentHealth - effectiveDamage);
    }

    @Override
    public List<RegularEnemyAccessory> accessories() {
        List<RegularEnemyAccessory> accessories = new ArrayList<>(wrapped.accessories());
        if (!accessories.contains(RegularEnemyAccessory.ARMOR)) {
            accessories.add(RegularEnemyAccessory.ARMOR);
        }
        return List.copyOf(accessories);
    }
}
