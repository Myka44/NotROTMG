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

    public static Builder builder() {
        return new Builder();
    }

    /** Builds statistics without relying on an error-prone list of numeric arguments. */
    public static final class Builder {
        private int maxHealth;
        private int maxMana;
        private int damage;
        private int defense;
        private double speed;
        private int spellStrength;

        private Builder() {
        }

        public Builder maxHealth(int maxHealth) {
            this.maxHealth = maxHealth;
            return this;
        }

        public Builder maxMana(int maxMana) {
            this.maxMana = maxMana;
            return this;
        }

        public Builder damage(int damage) {
            this.damage = damage;
            return this;
        }

        public Builder defense(int defense) {
            this.defense = defense;
            return this;
        }

        public Builder speed(double speed) {
            this.speed = speed;
            return this;
        }

        public Builder spellStrength(int spellStrength) {
            this.spellStrength = spellStrength;
            return this;
        }

        public EntityStats build() {
            return new EntityStats(maxHealth, maxMana, damage, defense, speed, spellStrength);
        }
    }
}
