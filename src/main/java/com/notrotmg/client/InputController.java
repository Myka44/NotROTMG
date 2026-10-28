package com.notrotmg.client;

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
    private final GameSessionFacade session;
    private final Set<KeyCode> pressedMovementKeys = EnumSet.noneOf(KeyCode.class);
    private volatile double attackTargetX;
    private volatile double attackTargetY;
    private volatile boolean firing;

    public InputController(GameSessionFacade session) {
        this.session = Objects.requireNonNull(session);
    }

    //stage pats window, o scene content
    public void attach(Scene scene, Stage stage, Canvas canvas) {
        Objects.requireNonNull(scene);
        Objects.requireNonNull(stage);
        Objects.requireNonNull(canvas);

        attackTargetX = canvas.getWidth() / 2;
        attackTargetY = canvas.getHeight() / 2;

        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                session.undo();
                event.consume();
                return;
            }
            if (event.isControlDown() && event.getCode() == KeyCode.E) {
                session.spawnEnemy(attackTargetX, attackTargetY);
                event.consume();
                return;
            }
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
        canvas.setOnMouseMoved(event -> {
            updateAttackTarget(event);
            if (firing) {
                publishAttackInput();
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
        session.move(horizontal, vertical);
    }

    private void updateAttackTarget(MouseEvent event) {
        attackTargetX = event.getX();
        attackTargetY = event.getY();
    }

    private void publishAttackInput() {
        session.attack(attackTargetX, attackTargetY, firing);
    }

    public void refreshAttackInput() {
        if (firing) {
            publishAttackInput();
        }
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
