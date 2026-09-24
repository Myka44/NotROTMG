package com.notrotmg.domain.item;

public final class Armor extends Equipment {
    private final int defenseBonus;

    public Armor(String name, ItemType type, EquipmentSlot slot, int defenseBonus) {
        super(name, requireArmorType(type), requireArmorSlot(slot));
        if (defenseBonus < 0) {
            throw new IllegalArgumentException("Armor defense bonus cannot be negative");
        }
        this.defenseBonus = defenseBonus;
    }

    public int defenseBonus() {
        return defenseBonus;
    }

    private static ItemType requireArmorType(ItemType type) {
        return switch (type) {
            case ROBE, LEATHER_ARMOR, HEAVY_ARMOR, HELMET, RING -> type;
            default -> throw new IllegalArgumentException("Armor must have an armor item type");
        };
    }

    private static EquipmentSlot requireArmorSlot(EquipmentSlot slot) {
        if (slot != EquipmentSlot.ARMOR && slot != EquipmentSlot.RING) {
            throw new IllegalArgumentException("Armor can only occupy the armor or ring slot");
        }
        return slot;
    }
}
