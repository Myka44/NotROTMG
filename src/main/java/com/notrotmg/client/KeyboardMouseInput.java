package com.notrotmg.client;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

public final class KeyboardMouseInput implements ControllerInput {
    private final Scene scene;
    private final Stage stage;
    private final Canvas canvas;
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);
    private Position target;
    private boolean firing;

    private final EventHandler<KeyEvent> keyPressed = this::keyPressed;
    private final EventHandler<KeyEvent> keyReleased = this::keyReleased;
    private final EventHandler<MouseEvent> mousePressed = this::mousePressed;
    private final EventHandler<MouseEvent> mouseMoved = this::mouseMoved;
    private final EventHandler<MouseEvent> mouseReleased = this::mouseReleased;
    private final ChangeListener<Boolean> focusChanged = (observable, oldValue, focused) -> {
        if (!focused) {
            pressedKeys.clear();
            firing = false;
        }
    };

    public KeyboardMouseInput(Scene scene, Stage stage, Canvas canvas) {
        this.scene = Objects.requireNonNull(scene);
        this.stage = Objects.requireNonNull(stage);
        this.canvas = Objects.requireNonNull(canvas);
        target = new Position(canvas.getWidth() / 2, canvas.getHeight() / 2);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.addEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, mouseMoved);
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, mouseMoved);
        scene.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleased);
        stage.focusedProperty().addListener(focusChanged);
    }

    @Override
    public void poll() {
        // JavaFX events already keep this device's state up to date.
    }

    @Override
    public Vector2 movement() {
        int horizontal = (isPressed(KeyCode.D, KeyCode.RIGHT) ? 1 : 0)
                - (isPressed(KeyCode.A, KeyCode.LEFT) ? 1 : 0);
        int vertical = (isPressed(KeyCode.S, KeyCode.DOWN) ? 1 : 0)
                - (isPressed(KeyCode.W, KeyCode.UP) ? 1 : 0);
        return new Vector2(horizontal, vertical);
    }

    @Override
    public Position aimTarget(Position playerScreenPosition) {
        return target;
    }

    @Override
    public boolean isFiring() {
        return firing;
    }

    @Override
    public void close() {
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, keyPressed);
        scene.removeEventFilter(KeyEvent.KEY_RELEASED, keyReleased);
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, mousePressed);
        canvas.removeEventHandler(MouseEvent.MOUSE_MOVED, mouseMoved);
        canvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, mouseMoved);
        scene.removeEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleased);
        stage.focusedProperty().removeListener(focusChanged);
        pressedKeys.clear();
        firing = false;
    }

    private void keyPressed(KeyEvent event) {
        if (!event.isControlDown() && isMovementKey(event.getCode())) {
            pressedKeys.add(event.getCode());
            event.consume();
        }
    }

    private void keyReleased(KeyEvent event) {
        if (isMovementKey(event.getCode())) {
            pressedKeys.remove(event.getCode());
            event.consume();
        }
    }

    private void mousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseMoved(event);
            firing = true;
            event.consume();
        }
    }

    private void mouseMoved(MouseEvent event) {
        target = new Position(event.getX(), event.getY());
    }

    private void mouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && firing) {
            firing = false;
            event.consume();
        }
    }

    private boolean isPressed(KeyCode first, KeyCode second) {
        return pressedKeys.contains(first) || pressedKeys.contains(second);
    }

    private static boolean isMovementKey(KeyCode code) {
        return switch (code) {
            case W, A, S, D, UP, DOWN, LEFT, RIGHT -> true;
            default -> false;
        };
    }
}
