package com.notrotmg.protocol.servertoclient;

import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.ItemType;

/** Client-visible description of an item. */
public record ItemSnapshot(
        String name,
        ItemType itemType,
        EquipmentSlot equipmentSlot
) {
}
