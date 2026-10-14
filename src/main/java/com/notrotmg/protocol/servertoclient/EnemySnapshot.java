package com.notrotmg.protocol.servertoclient;

import com.notrotmg.domain.enemy.EnemyType;

/** Public render state for one server-owned enemy. */
public record EnemySnapshot(
        String id,
        EnemyType type,
        int x,
        int y,
        int size,
        int currentHealth,
        int maxHealth
) {
}
