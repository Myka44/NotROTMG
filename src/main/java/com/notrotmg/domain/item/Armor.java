package com.notrotmg.domain.item;

public final class Armor extends Equipment {
    private final int defenseBonus;

    public Armor(String name, ItemType type, EquipmentSlot slot, int defenseBonus) {
        super(name, requireArmorType(type), requireSlotForType(type, slot));
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

    private static EquipmentSlot requireSlotForType(ItemType type, EquipmentSlot slot) {
        boolean correctSlot = switch (type) {
            case ROBE, LEATHER_ARMOR, HEAVY_ARMOR -> slot == EquipmentSlot.ARMOR;
            case HELMET -> slot == EquipmentSlot.ABILITY;
            case RING -> slot == EquipmentSlot.RING;
            default -> false;
        };
        if (!correctSlot) {
            throw new IllegalArgumentException(type + " cannot occupy the " + slot + " slot");
        }
        return slot;
    }
}
