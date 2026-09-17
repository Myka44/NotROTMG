package com.notrotmg.client;

import com.notrotmg.shared.StateSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel {
    private final Map<String, StateSnapshot.PlayerState> players = new ConcurrentHashMap<>();
    private final List<StateSnapshot.EnemyState> enemies = new CopyOnWriteArrayList<>();
    private final List<StateSnapshot.MapElementState> mapElements = new CopyOnWriteArrayList<>();
    private final Renderer renderer = new Renderer();
    private String localPlayerId;
    private int localX = 200;
    private int localY = 200;
    private int localColor = 0xFF0000;
    private static final int PLAYER_SPEED = 3;
    private static final int PLAYER_SIZE = 20;

    public GamePanel() {
        StateSnapshot.PlayerState localPlayer = new StateSnapshot.PlayerState("local", "You", 200, 200, 0xFF0000, 100);
        players.put(localPlayer.id(), localPlayer);
        localPlayerId = localPlayer.id();
        localColor = 0xFF0000;
        localX = 200;
        localY = 200;
    }

    public void setLocalPlayerId(String localPlayerId) {
        this.localPlayerId = localPlayerId;
    }

    public void setLocalColor(int color) {
        this.localColor = color;
    }

    public void moveLocalPlayer(int dx, int dy, int worldWidth, int worldHeight) {
        localX = clamp(localX + dx * PLAYER_SPEED, 0, worldWidth - PLAYER_SIZE);
        localY = clamp(localY + dy * PLAYER_SPEED, 0, worldHeight - PLAYER_SIZE);
        StateSnapshot.PlayerState local = new StateSnapshot.PlayerState(localPlayerId, "You", localX, localY, localColor, 100);
        players.put(localPlayerId, local);
        repaint();
    }

    public void mergeState(StateSnapshot snapshot, String playerId, boolean joined) {
        this.enemies.clear();
        this.enemies.addAll(snapshot.enemies);
        this.mapElements.clear();
        this.mapElements.addAll(snapshot.mapElements);

        this.players.clear();
        if (!joined && playerId != null) {
            StateSnapshot.PlayerState localPlayer = new StateSnapshot.PlayerState(playerId, "You", localX, localY, localColor, 100);
            this.players.put(localPlayer.id(), localPlayer);
        }
        this.players.putAll(snapshot.players);
        StateSnapshot.PlayerState serverLocal = this.players.get(localPlayerId);
        if (serverLocal != null) {
            localX = serverLocal.x();
            localY = serverLocal.y();
            if (joined) {
                localColor = serverLocal.color();
            }
        }
        repaint();
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        for (StateSnapshot.MapElementState element : mapElements) {
            renderer.drawMapElement(g2d, element.x(), element.y(), 30, Color.GRAY);
        }

        for (StateSnapshot.EnemyState enemy : enemies) {
            Color color = switch (enemy.type()) {
                case 0 -> Color.RED;
                case 1 -> Color.ORANGE;
                case 2 -> Color.MAGENTA;
                default -> Color.PINK;
            };
            renderer.drawEnemy(g2d, enemy.x(), enemy.y(), 20, color);
        }

        for (StateSnapshot.PlayerState player : players.values()) {
            Color color = new Color(player.color());
            String label = player.id().equals(localPlayerId) ? "You" : player.name();
            renderer.drawPlayer(g2d, player.x(), player.y(), 20, color, label);
        }
    }

    public void render(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.fillRect(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, size, size);
    }
}
