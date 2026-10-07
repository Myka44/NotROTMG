package com.notrotmg.protocol.servertoclient;

import com.notrotmg.domain.CharacterClass;

/** Immutable network representation of one player. */
public record PlayerSnapshot(
        String id,
        int x,
        int y,
        int size,
        int level,
        int currentHealth,
        int maxHealth,
        CharacterClass characterClass
) {
}
