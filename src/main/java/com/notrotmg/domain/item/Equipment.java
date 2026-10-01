package com.notrotmg.domain.item;

import java.util.Objects;

public abstract class Equipment extends Item {
    private final EquipmentSlot slot;

    protected Equipment(String name, ItemType type, EquipmentSlot slot) {
        super(name, type);
        this.slot = Objects.requireNonNull(slot);
    }

    public final EquipmentSlot slot() {
        return slot;
    }
}
