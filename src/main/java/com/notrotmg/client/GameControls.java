package com.notrotmg.client;

import com.notrotmg.common.logging.GameLogger;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

import java.util.Objects;

public final class GameControls implements AutoCloseable {
    private static final double MOVEMENT_THRESHOLD = 0.25;

    private final GameSessionFacade session;
    private final ControllerInput preferredInput;
    private final ControllerInput fallbackInput;
    private ControllerInput input;
    private Vector2 previousMovement;
    private Position previousTarget;
    private boolean previouslyFiring;
    private AnimationTimer timer;
    private Stage stage;
    private ChangeListener<Boolean> focusChanged;

    public GameControls(GameSessionFacade session, ControllerInput input) {
        this(session, input, input);
    }

    public GameControls(GameSessionFacade session, ControllerInput preferredInput, ControllerInput fallbackInput) {
        this.session = Objects.requireNonNull(session);
        this.preferredInput = Objects.requireNonNull(preferredInput);
        this.fallbackInput = Objects.requireNonNull(fallbackInput);
        this.input = fallbackInput;
    }

    public void start(Stage stage) {
        if (timer != null) {
            throw new IllegalStateException("Controls are already started");
        }
        this.stage = Objects.requireNonNull(stage);
        focusChanged = (observable, oldValue, focused) -> {
            if (!focused) {
                stop();
            }
        };
        stage.focusedProperty().addListener(focusChanged);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (stage.isFocused()) {
                    update();
                }
            }
        };
        timer.start();
    }

    public void update() {
        preferredInput.poll();
        ControllerInput selected = preferredInput.isConnected() ? preferredInput : fallbackInput;
        if (input != selected) {
            stop();
            input = selected;
            GameLogger.getInstance().log("Controls: " + input.getClass().getSimpleName());
        }
        if (input != preferredInput) {
            input.poll();
        }
        move();
        shoot();
    }

    public void move() {
        Vector2 direction = input.movement();
        Vector2 movement = new Vector2(digitalAxis(direction.x()), digitalAxis(direction.y()));
        if (!movement.equals(previousMovement)) {
            session.move((int) movement.x(), (int) movement.y());
            previousMovement = movement;
        }
    }

    public void shoot() {
        boolean firing = input.isFiring();
        Position target = input.aimTarget(session.playerScreenPosition());
        session.aimTarget(input != fallbackInput ? target : null);
        if (firing || previouslyFiring) {
            session.attack(target.x(), target.y(), firing);
            previousTarget = target;
            previouslyFiring = firing;
        }
    }

    public void stop() {
        if (previousMovement != null && !previousMovement.equals(Vector2.ZERO)) {
            session.move(0, 0);
        }
        if (previouslyFiring) {
            session.attack(previousTarget.x(), previousTarget.y(), false);
        }
        previousMovement = null;
        previousTarget = null;
        previouslyFiring = false;
        session.aimTarget(null);
    }

    @Override
    public void close() {
        if (timer != null) {
            timer.stop();
            stage.focusedProperty().removeListener(focusChanged);
        }
        stop();
        preferredInput.close();
        if (fallbackInput != preferredInput) {
            fallbackInput.close();
        }
    }

    private static int digitalAxis(double value) {
        return Math.abs(value) < MOVEMENT_THRESHOLD ? 0 : (value > 0 ? 1 : -1);
    }
}
