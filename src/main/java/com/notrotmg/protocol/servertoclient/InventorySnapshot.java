package com.notrotmg.protocol.servertoclient;

import com.notrotmg.domain.item.EquipmentSlot;

import java.util.List;
import java.util.Map;

/** Private inventory and equipment state sent only to its owning client. */
public record InventorySnapshot(
        List<InventorySlotSnapshot> inventorySlots,
        Map<EquipmentSlot, ItemSnapshot> equippedItems
) implements Snapshot {
    public InventorySnapshot {
        inventorySlots = List.copyOf(inventorySlots);
        equippedItems = Map.copyOf(equippedItems);
    }
}
