package com.notrotmg.client;

import com.notrotmg.protocol.clienttoserver.PlayerInput;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/** Translates JavaFX key state into transport-neutral player input messages. */
public final class InputController {
    private final GameClient gameClient;
    private final Set<KeyCode> pressedMovementKeys = EnumSet.noneOf(KeyCode.class);

    public InputController(GameClient gameClient) {
        this.gameClient = Objects.requireNonNull(gameClient);
    }

    //stage pats window, o scene content
    public void attach(Scene scene, Stage stage) {
        scene.setOnKeyPressed(event -> {
            if (isMovementKey(event.getCode()) && pressedMovementKeys.add(event.getCode())) {
                publishInput();
                event.consume();
            }
        });
        scene.setOnKeyReleased(event -> {
            if (isMovementKey(event.getCode()) && pressedMovementKeys.remove(event.getCode())) {
                publishInput();
                event.consume();
            }
        });
        //adds a listener for whenever the stage gains or loses focus
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused && !pressedMovementKeys.isEmpty()) {
                pressedMovementKeys.clear();
                publishInput();
            }
        });
    }

    private void publishInput() {
        int horizontal = (isPressed(KeyCode.D, KeyCode.RIGHT) ? 1 : 0)
                - (isPressed(KeyCode.A, KeyCode.LEFT) ? 1 : 0);
        int vertical = (isPressed(KeyCode.S, KeyCode.DOWN) ? 1 : 0)
                - (isPressed(KeyCode.W, KeyCode.UP) ? 1 : 0);
        gameClient.send(new PlayerInput(horizontal, vertical));
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
