package com.notrotmg.server;

import com.notrotmg.shared.Message;
import com.notrotmg.shared.MessageFactory;
import com.notrotmg.shared.StateSnapshot;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameState {
    private static volatile GameState instance;
    private final Map<String, Player> players = new ConcurrentHashMap<>();
    private final List<Enemy> enemies = new CopyOnWriteArrayList<>();
    private final List<MapElement> mapElements = new CopyOnWriteArrayList<>();
    private final int worldWidth = 800;
    private final int worldHeight = 600;
    private final GameLoop gameLoop;

    private GameState() {
        this.gameLoop = new GameLoop(this);
    }

    public static synchronized GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void addPlayer(Player player) {
        players.put(player.id, player);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public Map<String, Player> getPlayers() {
        return players;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<MapElement> getMapElements() {
        return mapElements;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies.clear();
        this.enemies.addAll(enemies);
    }

    public void setMapElements(List<MapElement> mapElements) {
        this.mapElements.clear();
        this.mapElements.addAll(mapElements);
    }

    public int getWorldWidth() {
        return worldWidth;
    }

    public int getWorldHeight() {
        return worldHeight;
    }

    public GameLoop getGameLoop() {
        return gameLoop;
    }

    public void start() {
        System.out.println("[STATE] GameState.start() called, gameLoop=" + gameLoop);
        gameLoop.start();
    }

    public void halt() {
        gameLoop.halt();
    }

    public Message createSnapshot() {
        Map<String, StateSnapshot.PlayerState> playerStates = new java.util.HashMap<>();
        for (Map.Entry<String, Player> entry : players.entrySet()) {
            Player p = entry.getValue();
            playerStates.put(p.id, new StateSnapshot.PlayerState(p.id, p.name, p.x, p.y, p.color, p.hp));
        }
        List<StateSnapshot.EnemyState> enemyStates = new java.util.ArrayList<>();
        for (Enemy e : enemies) {
            enemyStates.add(new StateSnapshot.EnemyState(e.id, e.x, e.y, e.type, e.hp));
        }
        List<StateSnapshot.MapElementState> elementStates = new java.util.ArrayList<>();
        for (MapElement m : mapElements) {
            elementStates.add(new StateSnapshot.MapElementState(m.id, m.x, m.y, m.type));
        }
        return MessageFactory.createStateSnapshot(playerStates, enemyStates, elementStates);
    }
}
