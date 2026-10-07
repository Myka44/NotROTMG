package com.notrotmg.protocol.clienttoserver;

/** Requests that the item in an inventory slot be equipped. */
public record EquipItemCommand(int inventorySlot) implements ClientCommand {
    public EquipItemCommand {
        if (inventorySlot < 0) {
            throw new IllegalArgumentException("Inventory slot cannot be negative");
        }
    }
}
