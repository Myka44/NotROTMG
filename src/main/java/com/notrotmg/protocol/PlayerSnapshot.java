package com.notrotmg.protocol;

/** Immutable network representation of one player. */
public record PlayerSnapshot(
        String id,
        int x,
        int y,
        int size,
        int level,
        int currentHealth,
        int maxHealth,
        String characterClass
) {
}
