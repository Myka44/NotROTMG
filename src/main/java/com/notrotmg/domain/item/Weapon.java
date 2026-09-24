package com.notrotmg.domain.item;

public final class Weapon extends Equipment {
    private final int damageBonus;

    public Weapon(String name, ItemType type, int damageBonus) {
        super(name, requireWeaponType(type), EquipmentSlot.WEAPON);
        if (damageBonus < 0) {
            throw new IllegalArgumentException("Weapon damage bonus cannot be negative");
        }
        this.damageBonus = damageBonus;
    }

    public int damageBonus() {
        return damageBonus;
    }

    private static ItemType requireWeaponType(ItemType type) {
        if (type != ItemType.SWORD && type != ItemType.STAFF && type != ItemType.BOW) {
            throw new IllegalArgumentException("A weapon must have a weapon item type");
        }
        return type;
    }
}
