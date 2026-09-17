package com.notrotmg.notrotmg;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/** Local WebSocket server with one authoritative player per connection. */
public final class LocalGameServer extends WebSocketServer implements AutoCloseable {
    public static final int DEFAULT_PORT = 7070;
    public static final int MAX_CLIENTS = 4;
    private static final String GAME_PATH = "/game";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GameWorld gameWorld = new GameWorld();
    private final Map<WebSocket, String> clientPlayerIds = new ConcurrentHashMap<>();
    private final AtomicInteger nextPlayerNumber = new AtomicInteger(1);
    private final CountDownLatch started = new CountDownLatch(1);
    private final ScheduledExecutorService gameLoop = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "game-loop");
        thread.setDaemon(true);
        return thread;
    });
    private volatile GameState lastBroadcastState = gameWorld.currentState();

    public LocalGameServer() {
        this(DEFAULT_PORT);
    }

    public LocalGameServer(int port) {
        super(new InetSocketAddress("127.0.0.1", port));
        setConnectionLostTimeout(30);
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        if (!GAME_PATH.equals(handshake.getResourceDescriptor())) {
            connection.close(1008, "Connect to " + GAME_PATH);
            return;
        }

        int clientCount;
        String playerId;
        synchronized (clientPlayerIds) {
            if (clientPlayerIds.size() >= MAX_CLIENTS) {
                connection.close(1013, "Server is full");
                return;
            }
            playerId = "player-" + nextPlayerNumber.getAndIncrement();
            clientPlayerIds.put(connection, playerId);
            gameWorld.addPlayer(playerId);
            clientCount = clientPlayerIds.size();
        }

        connection.send(toJson(gameWorld.currentState()));
        System.out.println(playerId + " connected (" + clientCount + "/" + MAX_CLIENTS + ")");
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        String playerId = clientPlayerIds.get(connection);
        if (playerId == null) {
            return;
        }

        try {
            gameWorld.setInput(playerId, objectMapper.readValue(message, MoveCommand.class));
        } catch (Exception exception) {
            System.err.println("Rejected client message: " + exception.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        String playerId = removePlayer(connection);
        if (playerId != null) {
            System.out.println(
                    playerId + " disconnected (" + clientPlayerIds.size() + "/" + MAX_CLIENTS + ")"
            );
        }
    }

    @Override
    public void onError(WebSocket connection, Exception error) {
        if (connection != null) {
            removePlayer(connection);
        }
        System.err.println("WebSocket error: " + error.getMessage());
    }

    @Override
    public void onStart() {
        long tickPeriod = 1_000_000_000L / GameWorld.TICKS_PER_SECOND;
        gameLoop.scheduleAtFixedRate(this::runGameTick, 0, tickPeriod, TimeUnit.NANOSECONDS);
        started.countDown();
        System.out.println("Local game server listening on ws://127.0.0.1:" + getPort() + GAME_PATH);
    }

    public boolean awaitStarted(long timeout, TimeUnit unit) throws InterruptedException {
        return started.await(timeout, unit);
    }

    private void runGameTick() {
        try {
            GameState state = gameWorld.tick();
            if (!state.equals(lastBroadcastState)) {
                lastBroadcastState = state;
                broadcast(toJson(state));
            }
        } catch (RuntimeException exception) {
            System.err.println("Game-loop error: " + exception.getMessage());
        }
    }

    private String removePlayer(WebSocket connection) {
        String playerId = clientPlayerIds.remove(connection);
        if (playerId != null) {
            gameWorld.removePlayer(playerId);
        }
        return playerId;
    }

    private String toJson(GameState state) {
        try {
            return objectMapper.writeValueAsString(state);
        } catch (Exception exception) {
            throw new IllegalStateException("Could not serialize game state", exception);
        }
    }

    @Override
    public void close() {
        gameLoop.shutdownNow();
        try {
            stop(1_000);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LocalGameServer server = new LocalGameServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        server.start();
        if (!server.awaitStarted(5, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Server did not start within five seconds");
        }
    }
}
