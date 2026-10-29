package com.notrotmg.protocol.servertoclient;

import java.util.Objects;

/** One completed attack or confirmed damage event, independent of presentation. */
public record CombatEvent(Kind kind, String spriteId) {
    public enum Kind {
        PLAYER_SHOT,
        ENEMY_SHOT,
        PLAYER_HIT,
        ENEMY_HIT
    }

    public CombatEvent {
        Objects.requireNonNull(kind);
        Objects.requireNonNull(spriteId);
        if ((kind == Kind.PLAYER_SHOT || kind == Kind.ENEMY_SHOT) && spriteId.isBlank()) {
            throw new IllegalArgumentException("Shot events require a projectile sprite ID");
        }
    }
}
