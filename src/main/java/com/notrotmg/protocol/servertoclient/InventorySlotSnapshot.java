package com.notrotmg.protocol.servertoclient;

/** One inventory slot. A null item means that the slot is empty. */
public record InventorySlotSnapshot(int index, ItemSnapshot item) {
    public InventorySlotSnapshot {
        if (index < 0) {
            throw new IllegalArgumentException("Inventory slot cannot be negative");
        }
    }
}
