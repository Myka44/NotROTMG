package com.notrotmg.domain.item;

public final class Potion extends Item {
    private final int restoredHealth;
    private final int restoredMana;

    public Potion(String name, int restoredHealth, int restoredMana) {
        super(name, ItemType.POTION);
        if (restoredHealth < 0 || restoredMana < 0 || restoredHealth + restoredMana == 0) {
            throw new IllegalArgumentException("A potion must restore health, mana, or both");
        }
        this.restoredHealth = restoredHealth;
        this.restoredMana = restoredMana;
    }

    public int restoredHealth() {
        return restoredHealth;
    }

    public int restoredMana() {
        return restoredMana;
    }
}
