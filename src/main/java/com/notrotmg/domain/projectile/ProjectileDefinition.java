package com.notrotmg.domain.projectile;

/** Immutable configuration shared by projectiles of the same kind. */
public record ProjectileDefinition(
        String spriteId,
        int damage,
        double speed,
        double size,
        double lifetimeSeconds
) {
    public ProjectileDefinition {
        if (spriteId == null || spriteId.isBlank()) {
            throw new IllegalArgumentException("Projectile sprite ID cannot be blank");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Projectile damage cannot be negative");
        }
        if (speed <= 0 || size <= 0 || lifetimeSeconds <= 0) {
            throw new IllegalArgumentException("Projectile speed, size and lifetime must be positive");
        }
    }
}
