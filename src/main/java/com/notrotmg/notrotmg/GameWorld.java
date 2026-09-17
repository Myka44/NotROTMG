package com.notrotmg.notrotmg;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Synchronized, authoritative state for all connected players. */
public final class GameWorld {
    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    public static final int SQUARE_SIZE = 32;
    public static final int TICKS_PER_SECOND = 30;
    private static final int MAX_PLAYERS = 4;
    private static final double SPEED_PIXELS_PER_SECOND = 180.0;
    private static final int[][] SPAWN_POSITIONS = {
            {64, 64},
            {CANVAS_WIDTH - SQUARE_SIZE - 64, 64},
            {64, CANVAS_HEIGHT - SQUARE_SIZE - 64},
            {CANVAS_WIDTH - SQUARE_SIZE - 64, CANVAS_HEIGHT - SQUARE_SIZE - 64}
    };

    private final Map<String, Player> players = new LinkedHashMap<>();

    public synchronized GameState addPlayer(String id) {
        if (players.containsKey(id)) {
            return currentState();
        }
        if (players.size() >= MAX_PLAYERS) {
            throw new IllegalStateException("The game is full");
        }

        int spawnSlot = firstAvailableSpawnSlot();
        int[] spawn = SPAWN_POSITIONS[spawnSlot];
        players.put(id, new Player(id, spawnSlot, spawn[0], spawn[1]));
        return currentState();
    }

    public synchronized GameState removePlayer(String id) {
        players.remove(id);
        return currentState();
    }

    public synchronized void setInput(String id, MoveCommand command) {
        Player player = players.get(id);
        if (player != null) {
            player.inputX = Integer.compare(command.dx(), 0);
            player.inputY = Integer.compare(command.dy(), 0);
        }
    }

    /** Advances every player by one fixed server tick. */
    public synchronized GameState tick() {
        double distance = SPEED_PIXELS_PER_SECOND / TICKS_PER_SECOND;
        for (Player player : players.values()) {
            double inputLength = Math.hypot(player.inputX, player.inputY);
            if (inputLength == 0.0) {
                continue;
            }

            player.x = clamp(
                    player.x + player.inputX / inputLength * distance,
                    0,
                    CANVAS_WIDTH - SQUARE_SIZE
            );
            player.y = clamp(
                    player.y + player.inputY / inputLength * distance,
                    0,
                    CANVAS_HEIGHT - SQUARE_SIZE
            );
        }
        return currentState();
    }

    public synchronized GameState currentState() {
        List<PlayerState> playerStates = players.values().stream()
                .map(player -> new PlayerState(
                        player.id,
                        (int) Math.round(player.x),
                        (int) Math.round(player.y),
                        SQUARE_SIZE
                ))
                .toList();
        return new GameState(CANVAS_WIDTH, CANVAS_HEIGHT, playerStates);
    }

    private int firstAvailableSpawnSlot() {
        for (int slot = 0; slot < SPAWN_POSITIONS.length; slot++) {
            int candidate = slot;
            boolean occupied = players.values().stream()
                    .anyMatch(player -> player.spawnSlot == candidate);
            if (!occupied) {
                return slot;
            }
        }
        throw new IllegalStateException("No spawn position is available");
    }

    private static double clamp(double value, double minimum, double maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }

    private static final class Player {
        private final String id;
        private final int spawnSlot;
        private double x;
        private double y;
        private int inputX;
        private int inputY;

        private Player(String id, int spawnSlot, double x, double y) {
            this.id = id;
            this.spawnSlot = spawnSlot;
            this.x = x;
            this.y = y;
        }
    }
}
