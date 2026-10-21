package com.notrotmg.protocol.clienttoserver;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Message sent by a client to request a server-side game action. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "commandType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayerInput.class, name = "player-input"),
        @JsonSubTypes.Type(value = AttackInput.class, name = "attack-input"),
        @JsonSubTypes.Type(value = EquipItemCommand.class, name = "equip-item"),
        @JsonSubTypes.Type(value = UnequipItemCommand.class, name = "unequip-item"),
        @JsonSubTypes.Type(value = DropItemCommand.class, name = "drop-item"),
        @JsonSubTypes.Type(value = SpawnEnemyRequest.class, name = "spawn-enemy"),
        @JsonSubTypes.Type(value = UndoCommand.class, name = "undo")
})
public interface ClientCommand {
}
