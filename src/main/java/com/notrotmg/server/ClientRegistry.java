package com.notrotmg.server;

import io.javalin.websocket.WsContext;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Owns the mapping between transport connections and domain player IDs. */
public final class ClientRegistry {
    private final int maximumClients;
    private final AtomicInteger nextPlayerNumber = new AtomicInteger(1);
    private final Map<WsContext, String> playerIdsByConnection = new ConcurrentHashMap<>();

    public ClientRegistry(int maximumClients) {
        if (maximumClients <= 0) {
            throw new IllegalArgumentException("Maximum clients must be positive");
        }
        this.maximumClients = maximumClients;
    }

    public synchronized Optional<Registration> register(WsContext connection) {
        if (playerIdsByConnection.size() >= maximumClients) {
            return Optional.empty();
        }

        String playerId = "player-" + nextPlayerNumber.getAndIncrement();
        playerIdsByConnection.put(connection, playerId);
        return Optional.of(new Registration(playerId, playerIdsByConnection.size()));
    }

    public Optional<String> playerId(WsContext connection) {
        return Optional.ofNullable(playerIdsByConnection.get(connection));
    }

    public Optional<String> unregister(WsContext connection) {
        return Optional.ofNullable(playerIdsByConnection.remove(connection));
    }

    public List<WsContext> connections() {
        return List.copyOf(playerIdsByConnection.keySet());
    }

    public int size() {
        return playerIdsByConnection.size();
    }

    public record Registration(String playerId, int connectedClients) {
    }
}
