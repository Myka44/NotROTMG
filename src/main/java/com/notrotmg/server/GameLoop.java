package com.notrotmg.server;

import com.notrotmg.shared.PlayerInput;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameLoop extends Thread {
    private final GameState gameState;
    private final Queue<PlayerInput> inputQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = false;
    private static final int TICK_RATE = 60;
    private static final int PLAYER_SPEED = 3;
    private static final int PLAYER_SIZE = 20;

    public GameLoop(GameState gameState) {
        this.gameState = gameState;
    }

    public void start() {
        System.out.println("[LOOP] GameLoop.start() called");
        running = true;
        super.start();
    }

    public void halt() {
        running = false;
        interrupt();
    }

    public void enqueueInput(PlayerInput input) {
        inputQueue.offer(input);
    }

    @Override
    public void run() {
        System.out.println("[LOOP] GameLoop thread running");
        int tickCount = 0;
        while (running) {
            try {
                long tickStart = System.currentTimeMillis();
                processInputs();
                updateEnemies();
                broadcastState();
                tickCount++;
                if (tickCount % 20 == 0) {
                    System.out.println("[LOOP] Tick " + tickCount + " players=" + gameState.getPlayers().size());
                }
                long elapsed = System.currentTimeMillis() - tickStart;
                long sleep = Math.max(0, 1000 / TICK_RATE - elapsed);
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                if (!running) break;
            } catch (Throwable t) {
                System.err.println("[LOOP] Throwable: " + t.getMessage());
                t.printStackTrace();
            }
        }
        System.out.println("[LOOP] GameLoop exited, total ticks=" + tickCount);
    }

    private void processInputs() {
        PlayerInput input;
        while ((input = inputQueue.poll()) != null) {
            Player player = gameState.getPlayer(input.playerId());
            if (player != null) {
                player.applyInput(input, PLAYER_SPEED, gameState.getWorldWidth(), gameState.getWorldHeight(), PLAYER_SIZE);
            }
        }
    }

    private void updateEnemies() {
        for (Enemy enemy : gameState.getEnemies()) {
            switch (enemy.type) {
                case 0:
                    enemy.x += 1;
                    if (enemy.x > gameState.getWorldWidth() - 20) enemy.x = 0;
                    break;
                case 1:
                    enemy.y += 1;
                    if (enemy.y > gameState.getWorldHeight() - 20) enemy.y = 0;
                    break;
                case 2:
                    enemy.x += (enemy.x < 400) ? 1 : -1;
                    break;
            }
        }
    }

    private void broadcastState() {
        try {
            GameEndpoint.broadcast(gameState.createSnapshot());
        } catch (Throwable t) {
            System.err.println("[LOOP] broadcastState threw: " + t.getMessage());
            t.printStackTrace();
        }
    }
}
