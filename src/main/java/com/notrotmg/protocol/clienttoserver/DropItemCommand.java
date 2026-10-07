package com.notrotmg.protocol.clienttoserver;

/** Requests that the item in an inventory slot be dropped. */
public record DropItemCommand(int inventorySlot) implements ClientCommand {
    public DropItemCommand {
        if (inventorySlot < 0) {
            throw new IllegalArgumentException("Inventory slot cannot be negative");
        }
    }
}
