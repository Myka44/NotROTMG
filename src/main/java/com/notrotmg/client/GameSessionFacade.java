package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.clienttoserver.DropItemCommand;
import com.notrotmg.protocol.clienttoserver.EquipItemCommand;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.clienttoserver.UnequipItemCommand;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.ServerMessage;
import javafx.scene.canvas.Canvas;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Facade over the client-side subsystems.
 *
 * <p>It presents one simple interface for connecting, sending player actions and
 * routing server messages, instead of making every caller wire the transport,
 * assets, rendering and inventory view together.</p>
 *
 * <p><b>Clients</b>: {@link GameClientApplication}, {@link InputController}.<br>
 * <b>Subsystems</b>: {@link GameClient}, {@link JacksonJsonCodec},
 * {@link SpriteCatalog}, {@link CanvasGameRenderer}, {@link InventoryPane},
 * {@link ClientMessageHandler}.</p>
 */
public final class GameSessionFacade {
    private final Canvas canvas;
    private final InventoryPane inventoryPane;
    private final ClientMessageHandler messageHandler;
    private final GameClient gameClient;

    public GameSessionFacade(Canvas canvas, URI serverUri) {
        this.canvas = Objects.requireNonNull(canvas);
        SpriteCatalog spriteCatalog = new SpriteCatalog();
        CanvasGameRenderer renderer = new CanvasGameRenderer(canvas, spriteCatalog);
        this.inventoryPane = new InventoryPane(
                spriteCatalog,
                this::equipItem,
                this::dropItem,
                this::unequipItem
        );
        this.messageHandler = new ClientMessageHandler(renderer, inventoryPane);
        this.gameClient = new GameClient(serverUri, new JacksonJsonCodec());
        renderer.render(emptySnapshot());
    }

    /** The shared game canvas, already wired to the renderer subsystem. */
    public Canvas canvas() {
        return canvas;
    }

    /** The inventory/equipment view, already wired to this session's transport. */
    public InventoryPane inventoryView() {
        return inventoryPane;
    }

    public void addListener(GameClientListener listener) {
        gameClient.addListener(listener);
    }

    public void removeListener(GameClientListener listener) {
        gameClient.removeListener(listener);
    }

    public void connect() {
        gameClient.connect();
    }

    public void close() {
        gameClient.close();
    }

    /** Routes a decoded server message to the view subsystem that owns it. */
    public void handleServerMessage(ServerMessage message) {
        messageHandler.handle(message);
    }

    public void move(int horizontal, int vertical) {
        gameClient.send(new PlayerInput(horizontal, vertical));
    }

    public void attack(double targetX, double targetY, boolean firing) {
        gameClient.send(new AttackInput(targetX, targetY, firing));
    }

    public void equipItem(int inventorySlot) {
        gameClient.send(new EquipItemCommand(inventorySlot));
    }

    public void unequipItem(EquipmentSlot equipmentSlot) {
        gameClient.send(new UnequipItemCommand(equipmentSlot));
    }

    public void dropItem(int inventorySlot) {
        gameClient.send(new DropItemCommand(inventorySlot));
    }

    private static GameSnapshot emptySnapshot() {
        return new GameSnapshot(
                GameRules.CANVAS_WIDTH,
                GameRules.CANVAS_HEIGHT,
                List.of(),
                List.of(),
                List.of()
        );
    }
}
