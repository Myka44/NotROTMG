package com.notrotmg.client;

import com.notrotmg.common.logging.GameLogger;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.Configuration;

import java.util.Locale;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;

public final class DualSenseInput implements ControllerInput {
    private static final double DEAD_ZONE = 0.2;
    private static final double AIM_DISTANCE = 200;
    private static final float TRIGGER_THRESHOLD = -0.5f;

    private GLFWGamepadState state;
    private boolean initialized;
    private boolean unavailable;
    private boolean closed;
    private boolean connected;
    private boolean firing;
    private boolean triggerArmed;
    private int joystick = -1;
    private Vector2 movement = Vector2.ZERO;
    private Vector2 aimDirection = new Vector2(1, 0);

    @Override
    public void poll() {
        if (closed || unavailable) {
            return;
        }
        try {
            if (!initialized) {
                if (System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("mac")) {
                    Configuration.GLFW_LIBRARY_NAME.set("glfw_async");
                }
                if (!glfwInit()) {
                    unavailable = true;
                    GameLogger.getInstance().log("Gamepad initialization failed; keyboard/mouse remain available.");
                    return;
                }
                initialized = true;
                state = GLFWGamepadState.calloc();
            }
            glfwPollEvents();
            if (joystick < 0 || !glfwJoystickIsGamepad(joystick)) {
                joystick = findGamepad();
            }
            if (joystick < 0 || !glfwGetGamepadState(joystick, state)) {
                clearState();
                joystick = -1;
                return;
            }
            readState(state);
        } catch (RuntimeException | LinkageError error) {
            unavailable = true;
            clearState();
            GameLogger.getInstance().log("Gamepad unavailable; keyboard/mouse remain available: " + error);
        }
    }

    @Override
    public Vector2 movement() {
        return movement;
    }

    @Override
    public Position aimTarget(Position playerScreenPosition) {
        return Objects.requireNonNull(playerScreenPosition).translated(aimDirection, AIM_DISTANCE);
    }

    @Override
    public boolean isFiring() {
        return firing;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }
        closed = true;
        clearState();
        if (state != null) {
            state.free();
        }
        if (initialized) {
            glfwTerminate();
        }
    }

    void readState(GLFWGamepadState state) {
        connected = true;
        movement = stick(state.axes(GLFW_GAMEPAD_AXIS_LEFT_X), state.axes(GLFW_GAMEPAD_AXIS_LEFT_Y));
        Vector2 aim = stick(state.axes(GLFW_GAMEPAD_AXIS_RIGHT_X), state.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y));
        if (!aim.equals(Vector2.ZERO)) {
            aimDirection = aim.normalized();
        }
        float trigger = state.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER);
        if (trigger <= TRIGGER_THRESHOLD) {
            triggerArmed = true;
        }
        firing = triggerArmed && trigger > TRIGGER_THRESHOLD;
    }

    private void clearState() {
        connected = false;
        movement = Vector2.ZERO;
        aimDirection = new Vector2(1, 0);
        firing = false;
        triggerArmed = false;
    }

    private static Vector2 stick(float x, float y) {
        return Math.hypot(x, y) < DEAD_ZONE ? Vector2.ZERO : new Vector2(x, y);
    }

    private static int findGamepad() {
        for (int id = GLFW_JOYSTICK_1; id <= GLFW_JOYSTICK_LAST; id++) {
            if (glfwJoystickIsGamepad(id)) {
                return id;
            }
        }
        return -1;
    }
}
