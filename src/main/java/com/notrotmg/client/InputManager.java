package com.notrotmg.client;

import com.notrotmg.client.NetworkClient;
import com.notrotmg.shared.PlayerInput;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class InputManager extends KeyAdapter {
    private final NetworkClient networkClient;
    private final GamePanel gamePanel;
    private final Map<Integer, Boolean> pressedKeys = new HashMap<>();
    private String playerId;
    private Timer inputTimer;

    public InputManager(NetworkClient networkClient, GamePanel gamePanel) {
        this.networkClient = networkClient;
        this.gamePanel = gamePanel;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.put(e.getKeyCode(), true);
        startInputTimer();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    private void startInputTimer() {
        if (inputTimer != null && inputTimer.isRunning()) {
            return;
        }
        inputTimer = new Timer(1000 / 60, e -> sendInput());
        inputTimer.start();
    }

    private void sendInput() {
        if (playerId == null || pressedKeys.isEmpty()) {
            return;
        }
        int dx = 0;
        int dy = 0;
        if (pressedKeys.getOrDefault(KeyEvent.VK_W, false)) dy -= 1;
        if (pressedKeys.getOrDefault(KeyEvent.VK_S, false)) dy += 1;
        if (pressedKeys.getOrDefault(KeyEvent.VK_A, false)) dx -= 1;
        if (pressedKeys.getOrDefault(KeyEvent.VK_D, false)) dx += 1;

        if (dx != 0 || dy != 0) {
            PlayerInput input = new PlayerInput(playerId, dx, dy);
            networkClient.send(input.toJson());
            gamePanel.moveLocalPlayer(dx, dy, 800, 600);
        }
    }
}
