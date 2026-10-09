package com.notrotmg.domain.projectile;

public record ProjectileSpec(
        String spriteId,
        int damage,
        double speed,
        double length,
        double lifetimeSeconds
) {
    public ProjectileSpec {
        if (spriteId == null || spriteId.isBlank()) {
            throw new IllegalArgumentException("Projectile sprite ID cannot be blank");
        }
        if (damage < 0) {
            throw new IllegalArgumentException("Projectile damage cannot be negative");
        }
        if (speed <= 0 || length <= 0 || lifetimeSeconds <= 0) {
            throw new IllegalArgumentException("Projectile speed, length and lifetime must be positive");
        }
    }
}
