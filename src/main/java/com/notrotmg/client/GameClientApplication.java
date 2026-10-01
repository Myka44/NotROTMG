package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.protocol.GameProtocol;
import com.notrotmg.protocol.clienttoserver.DropItemCommand;
import com.notrotmg.protocol.clienttoserver.EquipItemCommand;
import com.notrotmg.protocol.clienttoserver.UnequipItemCommand;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;

public final class GameClientApplication extends Application {
    private final Canvas canvas = new Canvas(GameRules.CANVAS_WIDTH, GameRules.CANVAS_HEIGHT);
    private final Label statusLabel = new Label("Disconnected");
    private GameClient gameClient;

    @Override
    public void start(Stage stage) {
        SpriteCatalog spriteCatalog = new SpriteCatalog();
        CanvasGameRenderer renderer = new CanvasGameRenderer(canvas, spriteCatalog);
        renderer.render(new GameSnapshot(GameRules.CANVAS_WIDTH, GameRules.CANVAS_HEIGHT, List.of()));

        Label instructions = new Label("Move: WASD / arrow keys     Select an item, then Equip or Drop");
        instructions.getStyleClass().add("instructions-label");

        InventoryPane inventoryPane = new InventoryPane(
                spriteCatalog,
                this::equipItem,
                this::dropItem,
                this::unequipItem
        );
        ClientMessageHandler messageHandler = new ClientMessageHandler(renderer, inventoryPane);
        StackPane gameFrame = new StackPane(canvas);
        gameFrame.setPadding(new Insets(3));
        gameFrame.getStyleClass().add("game-frame");

        BorderPane root = new BorderPane(gameFrame);
        root.getStyleClass().add("game-root");
        root.setTop(instructions);
        root.setRight(inventoryPane);
        root.setBottom(statusLabel);
        BorderPane.setMargin(instructions, new Insets(8));
        BorderPane.setMargin(statusLabel, new Insets(8));
        BorderPane.setMargin(inventoryPane, new Insets(0, 0, 0, 8));
        statusLabel.getStyleClass().add("status-label");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("game.css"),
                "Missing client stylesheet: game.css"
        ).toExternalForm());
        gameClient = new GameClient(
                GameProtocol.LOCAL_SERVER_URI,
                new JacksonJsonCodec(),
                message -> Platform.runLater(() -> messageHandler.handle(message)),
                status -> Platform.runLater(() -> statusLabel.setText(status))
        );
        new InputController(gameClient).attach(scene, stage);

        stage.setTitle("NotROTMG local client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        statusLabel.setText("Connecting to " + GameProtocol.LOCAL_SERVER_URI + " ...");
        gameClient.connect();
    }

    @Override
    public void stop() {
        if (gameClient != null) {
            gameClient.close();
        }
    }

    private void equipItem(int inventorySlot) {
        if (gameClient != null) {
            gameClient.send(new EquipItemCommand(inventorySlot));
        }
    }

    private void unequipItem(EquipmentSlot equipmentSlot) {
        if (gameClient != null) {
            gameClient.send(new UnequipItemCommand(equipmentSlot));
        }
    }

    private void dropItem(int inventorySlot) {
        if (gameClient != null) {
            gameClient.send(new DropItemCommand(inventorySlot));
        }
    }

}
