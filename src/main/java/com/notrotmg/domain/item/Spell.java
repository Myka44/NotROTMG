package com.notrotmg.domain.item;

public final class Spell extends Equipment {
    private final int power;
    private final int manaCost;

    public Spell(String name, int power, int manaCost) {
        super(name, ItemType.SPELL, EquipmentSlot.ABILITY);
        if (power < 0 || manaCost < 0) {
            throw new IllegalArgumentException("Spell power and mana cost cannot be negative");
        }
        this.power = power;
        this.manaCost = manaCost;
    }

    public int power() {
        return power;
    }

    public int manaCost() {
        return manaCost;
    }
}
