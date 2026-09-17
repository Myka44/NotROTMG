package com.notrotmg.server;

import com.notrotmg.shared.MovementStrategy;
import com.notrotmg.shared.PlayerInput;

import java.util.Objects;

public class Player {
    public final String id;
    public final String name;
    public final int color;
    public int x;
    public int y;
    public int hp;
    public MovementStrategy movementStrategy;

    public Player(String id, String name, int color, int x, int y, int hp) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.movementStrategy = new DefaultMovementStrategy();
    }

    public void applyInput(PlayerInput input, int speed, int worldWidth, int worldHeight, int size) {
        movementStrategy = Objects.requireNonNullElse(movementStrategy, new DefaultMovementStrategy());
        int newX = movementStrategy.calculateDx(x, input.dx(), speed);
        int newY = movementStrategy.calculateDy(y, input.dy(), speed);
        x = clamp(newX, 0, worldWidth - size);
        y = clamp(newY, 0, worldHeight - size);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static class DefaultMovementStrategy implements MovementStrategy {
        @Override
        public int calculateDx(int currentX, int inputDx, int speed) {
            return currentX + inputDx * speed;
        }

        @Override
        public int calculateDy(int currentY, int inputDy, int speed) {
            return currentY + inputDy * speed;
        }
    }
}
