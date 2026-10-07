package com.notrotmg.protocol.servertoclient;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/** Message published by the server to a connected client. */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "messageType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GameSnapshot.class, name = "game-snapshot"),
        @JsonSubTypes.Type(value = InventorySnapshot.class, name = "inventory-snapshot")
})
public interface ServerMessage {
}
