package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.clienttoserver.ClientCommand;
import com.notrotmg.protocol.clienttoserver.DropItemCommand;
import com.notrotmg.protocol.clienttoserver.EquipItemCommand;
import com.notrotmg.protocol.clienttoserver.JoinGameCommand;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.clienttoserver.SpawnEnemyRequest;
import com.notrotmg.protocol.clienttoserver.UnequipItemCommand;
import com.notrotmg.protocol.clienttoserver.UndoCommand;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.ServerMessage;
import com.notrotmg.protocol.servertoclient.Snapshot;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Facade over the client-side subsystems.
 *
 * <p>{@link #start(Scene, Stage, GameClientListener, GameControls, CharacterClass)} is the single high-level
 * operation: it renders the first frame, starts controls, routes server messages
 * and opens the connection by calling the smaller facade operations in order.
 * Clients no longer wire those steps themselves.</p>
 *
 * <p><b>Clients</b>: {@link GameClientApplication}, {@link GameControls}.<br>
 * <b>Subsystems</b>: {@link GameClient}, {@link JacksonJsonCodec},
 * {@link SpriteCatalog}, {@link CanvasGameRenderer}, {@link InventoryPane},
 * {@link ClientMessageHandler}.</p>
 */
public final class GameSessionFacade {
    private final Canvas canvas;
    private final SpriteCatalog spriteCatalog;
    private final CanvasGameRenderer renderer;
    private final InventoryPane inventoryPane;
    private final ClientMessageHandler messageHandler;
    private final GameClient gameClient;
    private final AudioAdapter audio;
    private final GameSoundListener soundListener;
    private GameClientListener routingListener;
    private GameControls controls;
    private AnimationTimer controlsTimer;
    private Stage controlsStage;
    private ChangeListener<Boolean> focusChanged;

    public GameSessionFacade(Canvas canvas, URI serverUri) {
        this.canvas = Objects.requireNonNull(canvas);
        this.spriteCatalog = new SpriteCatalog();
        this.renderer = new CanvasGameRenderer(canvas, spriteCatalog);
        this.inventoryPane = new InventoryPane(
                spriteCatalog,
                this::equipItem,
                this::dropItem,
                this::unequipItem
        );
        this.messageHandler = new ClientMessageHandler(renderer, inventoryPane);
        this.gameClient = new GameClient(serverUri, new JacksonJsonCodec());
        this.audio = new AudioAdapter();
        this.soundListener = new GameSoundListener(audio);
        this.gameClient.addListener(soundListener);
    }

    /**
     * Starts the client session. This is the facade's coarse-grained entry point:
     * it orchestrates the smaller facade operations instead of making the client
     * call each subsystem itself.
     */
    public void start(Scene scene, Stage stage, GameClientListener statusListener,
                      GameControls controls, CharacterClass characterClass) {
        if (controlsTimer != null) {
            throw new IllegalStateException("Controls are already started");
        }
        Objects.requireNonNull(scene);
        Objects.requireNonNull(stage);
        Objects.requireNonNull(statusListener);
        Objects.requireNonNull(characterClass);
        this.controls = Objects.requireNonNull(controls);

        renderInitialFrame();
        startControls(stage);
        routeServerMessages(statusListener, characterClass);
        connect();
    }

    public Image classSprite(CharacterClass characterClass) {
        return spriteCatalog.playerSprite(characterClass);
    }

    /** The inventory/equipment view, already wired to this session's transport. */
    public InventoryPane inventoryView() {
        return inventoryPane;
    }

    public void connect() {
        gameClient.connect();
    }

    public void close() {
        if (controlsTimer != null) {
            controlsTimer.stop();
            controlsStage.focusedProperty().removeListener(focusChanged);
        }
        if (controls != null) {
            controls.close();
        }
        gameClient.removeListener(soundListener);
        if (routingListener != null) {
            gameClient.removeListener(routingListener);
            routingListener = null;
        }
        gameClient.close();
        audio.close();
    }

    public void move(int horizontal, int vertical) {
        send(new PlayerInput(horizontal, vertical));
    }

    public void attack(double targetX, double targetY, boolean firing) {
        send(new AttackInput(
            renderer.worldX(targetX),
            renderer.worldY(targetY),
            firing
        ));
    }

    public void equipItem(int inventorySlot) {
        send(new EquipItemCommand(inventorySlot));
    }

    public Position playerScreenPosition() {
        return renderer.playerScreenPosition();
    }

    public void aimTarget(Position target) {
        renderer.aimTarget(target);
    }

    public void unequipItem(EquipmentSlot equipmentSlot) {
        send(new UnequipItemCommand(equipmentSlot));
    }

    public void dropItem(int inventorySlot) {
        send(new DropItemCommand(inventorySlot));
    }

    public void spawnEnemy(double x, double y) {
        send(new SpawnEnemyRequest(renderer.worldX(x), renderer.worldY(y)));
    }

    public void undo() {
        send(new UndoCommand());
    }

    private void send(ClientCommand command) {
        gameClient.send(command);
    }

    private void renderInitialFrame() {
        renderer.render(emptySnapshot());
    }

    private void startControls(Stage stage) {
        controlsStage = stage;
        focusChanged = (observable, oldValue, focused) -> {
            if (!focused) {
                controls.stop();
            }
        };
        stage.focusedProperty().addListener(focusChanged);
        controlsTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (stage.isFocused()) {
                    controls.update();
                    controls.move();
                    controls.shoot();
                }
            }
        };
        controlsTimer.start();
    }

    private void routeServerMessages(GameClientListener statusListener, CharacterClass characterClass) {
        routingListener = new GameClientListener() {
            @Override
            public void onConnected() {
                send(new JoinGameCommand(characterClass));
                Platform.runLater(() -> controls.stop());
                statusListener.onConnected();
            }

            @Override
            public void onMessage(ServerMessage message) {
                if (message instanceof Snapshot) {
                    Platform.runLater(() -> messageHandler.handle(message));
                }
            }

            @Override
            public void onDisconnected(int statusCode, String reason) {
                statusListener.onDisconnected(statusCode, reason);
            }

            @Override
            public void onError(String message) {
                statusListener.onError(message);
            }
        };
        gameClient.addListener(routingListener);
    }

    private static GameSnapshot emptySnapshot() {
        return new GameSnapshot(
            GameRules.WORLD_WIDTH,
            GameRules.WORLD_HEIGHT,
                List.of(),
                List.of(),
            List.of(),
            null
        );
    }
}
