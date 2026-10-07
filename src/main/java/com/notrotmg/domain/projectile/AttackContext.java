package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

import java.util.Objects;

/** Information that varies for each individual attack. */
public record AttackContext(
        String attackerId,
        Position origin,
        Vector2 aimDirection
) {
    public AttackContext {
        if (attackerId == null || attackerId.isBlank()) {
            throw new IllegalArgumentException("Attacker ID cannot be blank");
        }
        Objects.requireNonNull(origin);
        Objects.requireNonNull(aimDirection);

        aimDirection = aimDirection.normalized();
        if (aimDirection.equals(Vector2.ZERO)) {
            throw new IllegalArgumentException("Attack direction cannot be zero");
        }
    }
}
