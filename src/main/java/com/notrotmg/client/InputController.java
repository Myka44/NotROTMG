package com.notrotmg.client;

import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/** Translates JavaFX key state into transport-neutral player input messages. */
public final class InputController {
    private final GameClient gameClient;
    private final CanvasGameRenderer gameRenderer;
    private final Set<KeyCode> pressedMovementKeys = EnumSet.noneOf(KeyCode.class);
    private Canvas canvas;
    private double pointerX;
    private double pointerY;
    private double lastAimCameraX;
    private double lastAimCameraY;
    private boolean hasPointer;
    private double attackTargetX;
    private double attackTargetY;
    private boolean firing;

    public InputController(GameClient gameClient, CanvasGameRenderer gameRenderer) {
        this.gameClient = Objects.requireNonNull(gameClient);
        this.gameRenderer = Objects.requireNonNull(gameRenderer);
    }

    //stage pats window, o scene content
    public void attach(Scene scene, Stage stage, Canvas canvas) {
        Objects.requireNonNull(scene);
        Objects.requireNonNull(stage);
        this.canvas = Objects.requireNonNull(canvas);

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

        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                updateAttackTarget(event);
                firing = true;
                publishAttackInput();
                event.consume();
            }
        });
        canvas.setOnMouseDragged(event -> {
            if (firing) {
                updateAttackTarget(event);
                publishAttackInput();
                event.consume();
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
            if (event.getButton() == MouseButton.PRIMARY && firing) {
                firing = false;
                publishAttackInput();
            }
        });

        //adds a listener for whenever the stage gains or loses focus
        stage.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            if (!isFocused && !pressedMovementKeys.isEmpty()) {
                pressedMovementKeys.clear();
                publishInput();
            }
            if (!isFocused && firing) {
                firing = false;
                publishAttackInput();
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

    private void updateAttackTarget(MouseEvent event) {
        Point2D canvasPoint = canvas.sceneToLocal(event.getSceneX(), event.getSceneY());
        pointerX = canvasPoint.getX();
        pointerY = canvasPoint.getY();
        hasPointer = true;
        updateWorldAttackTarget();
    }

    public void refreshAimAfterCameraUpdate() {
        if (!firing || !hasPointer) {
            return;
        }

        double cameraX = gameRenderer.worldX(0);
        double cameraY = gameRenderer.worldY(0);
        if (cameraX != lastAimCameraX || cameraY != lastAimCameraY) {
            updateWorldAttackTarget();
            publishAttackInput();
        }
    }

    private void updateWorldAttackTarget() {
        lastAimCameraX = gameRenderer.worldX(0);
        lastAimCameraY = gameRenderer.worldY(0);
        attackTargetX = pointerX + lastAimCameraX;
        attackTargetY = pointerY + lastAimCameraY;
    }

    private void publishAttackInput() {
        gameClient.send(new AttackInput(attackTargetX, attackTargetY, firing));
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
