package com.notrotmg.client;

import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;
import com.notrotmg.protocol.servertoclient.ServerMessage;

import java.util.Objects;

/** Routes decoded server messages to the client view that owns their presentation. */
public final class ClientMessageHandler {
    private final CanvasGameRenderer gameRenderer;
    private final InventoryPane inventoryPane;

    public ClientMessageHandler(CanvasGameRenderer gameRenderer, InventoryPane inventoryPane) {
        this.gameRenderer = Objects.requireNonNull(gameRenderer);
        this.inventoryPane = Objects.requireNonNull(inventoryPane);
    }

    public void handle(ServerMessage message) {
        Objects.requireNonNull(message);

        if (message instanceof GameSnapshot gameSnapshot) {
            gameRenderer.render(gameSnapshot);
            return;
        }
        if (message instanceof InventorySnapshot inventorySnapshot) {
            inventoryPane.render(inventorySnapshot);
            return;
        }

        throw new IllegalArgumentException(
                "Unsupported server message: " + message.getClass().getSimpleName()
        );
    }
}
