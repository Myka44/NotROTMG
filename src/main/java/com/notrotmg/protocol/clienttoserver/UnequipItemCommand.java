package com.notrotmg.protocol.clienttoserver;

import com.notrotmg.domain.item.EquipmentSlot;

import java.util.Objects;

/** Requests that equipment be returned to an empty inventory slot. */
public record UnequipItemCommand(EquipmentSlot equipmentSlot) implements ClientCommand {
    public UnequipItemCommand {
        Objects.requireNonNull(equipmentSlot, "Equipment slot cannot be null");
    }
}
