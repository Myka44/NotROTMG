package com.notrotmg.domain;

/** Core statistics shared by characters and future enemies. */
public record EntityStats(
        int maxHealth,
        int maxMana,
        int damage,
        int defense,
        double speed,
        int spellStrength
) {
    public EntityStats {
        if (maxHealth <= 0 || maxMana < 0 || damage < 0 || defense < 0 || speed < 0 || spellStrength < 0) {
            throw new IllegalArgumentException("Entity statistics cannot be negative");
        }
    }
}
