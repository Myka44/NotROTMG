package com.notrotmg.notrotmg;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.Set;

public class HelloApplication extends Application {
    private final Canvas canvas = new Canvas(GameWorld.CANVAS_WIDTH, GameWorld.CANVAS_HEIGHT);
    private final Label statusLabel = new Label("Disconnected");
    private final Set<KeyCode> pressedMovementKeys = EnumSet.noneOf(KeyCode.class);
    private GameWebSocketClient gameClient;

    @Override
    public void start(Stage stage) {
        Label instructions = new Label("Move with WASD or the arrow keys");
        BorderPane root = new BorderPane(canvas);
        root.setTop(instructions);
        root.setBottom(statusLabel);
        BorderPane.setMargin(instructions, new Insets(8));
        BorderPane.setMargin(statusLabel, new Insets(8));

        draw(new GameWorld().currentState());

        Scene scene = new Scene(root);
        scene.setOnKeyPressed(event -> {
            if (gameClient != null
                    && isMovementKey(event.getCode())
                    && pressedMovementKeys.add(event.getCode())) {
                sendCurrentInput();
                event.consume();
            }
        });
        scene.setOnKeyReleased(event -> {
            if (gameClient != null
                    && isMovementKey(event.getCode())
                    && pressedMovementKeys.remove(event.getCode())) {
                sendCurrentInput();
                event.consume();
            }
        });

        stage.setTitle("NotROTMG local client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        gameClient = new GameWebSocketClient(
                state -> Platform.runLater(() -> draw(state)),
                status -> Platform.runLater(() -> statusLabel.setText(status))
        );
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused && !pressedMovementKeys.isEmpty()) {
                pressedMovementKeys.clear();
                sendCurrentInput();
            }
        });
    }

    @Override
    public void stop() {
        if (gameClient != null) {
            gameClient.close();
        }
    }

    private void draw(GameState state) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(Color.web("#171923"));
        graphics.fillRect(0, 0, state.canvasWidth(), state.canvasHeight());
        graphics.setFill(Color.web("#55d6be"));
        for (PlayerState player : state.players()) {
            graphics.fillRect(player.x(), player.y(), player.size(), player.size());
        }
    }

    private void sendCurrentInput() {
        int horizontal = (isPressed(KeyCode.D, KeyCode.RIGHT) ? 1 : 0)
                - (isPressed(KeyCode.A, KeyCode.LEFT) ? 1 : 0);
        int vertical = (isPressed(KeyCode.S, KeyCode.DOWN) ? 1 : 0)
                - (isPressed(KeyCode.W, KeyCode.UP) ? 1 : 0);
        gameClient.sendInput(horizontal, vertical);
    }

    private boolean isPressed(KeyCode first, KeyCode second) {
        return pressedMovementKeys.contains(first) || pressedMovementKeys.contains(second);
    }

    private static boolean isMovementKey(KeyCode code) {
        return switch (code) {
            case W, A, S, D, UP, DOWN, LEFT, RIGHT -> true;
            default -> false;
        };
    }
}
