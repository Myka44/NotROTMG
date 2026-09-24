package com.notrotmg.server;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.domain.GameRules;
import com.notrotmg.protocol.GameProtocol;
import com.notrotmg.protocol.GameSnapshot;
import com.notrotmg.protocol.PlayerInput;
import io.javalin.Javalin;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsMessageContext;

import java.util.Map;
import java.util.Objects;

/** Javalin transport adapter for the authoritative game world. */
public final class GameServer implements AutoCloseable {
    private final String host;
    private final int port;
    private final JacksonJsonCodec jsonCodec;
    private final AuthoritativeGameWorld world;
    private final ClientRegistry clients;
    private final GameLoop gameLoop;
    private volatile GameSnapshot lastPublishedSnapshot;
    private Javalin application;

    public GameServer() {
        this(
                GameProtocol.HOST,
                GameProtocol.PORT,
                new JacksonJsonCodec(),
                new AuthoritativeGameWorld(),
                new ClientRegistry(GameProtocol.MAX_PLAYERS),
                new GameLoop(
                        GameRules.TICKS_PER_SECOND,
                        error -> System.err.println("Game-loop error: " + error.getMessage())
                )
        );
    }

    public GameServer(
            String host,
            int port,
            JacksonJsonCodec jsonCodec,
            AuthoritativeGameWorld world,
            ClientRegistry clients,
            GameLoop gameLoop
    ) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.jsonCodec = Objects.requireNonNull(jsonCodec);
        this.world = Objects.requireNonNull(world);
        this.clients = Objects.requireNonNull(clients);
        this.gameLoop = Objects.requireNonNull(gameLoop);
        this.lastPublishedSnapshot = world.snapshot();
    }

    public synchronized void start() {
        if (application != null) {
            throw new IllegalStateException("Game server is already running");
        }

        application = Javalin.create(config -> {
            config.jetty.host = host;
            config.jetty.port = port;
            config.routes.get("/health", context -> {
                context.contentType("application/json");
                context.result(jsonCodec.encode(Map.of(
                        "status", "ok",
                        "players", clients.size()
                )));
            });
            config.routes.ws(GameProtocol.WEBSOCKET_PATH, websocket -> {
                websocket.onConnect(this::onConnect);
                websocket.onMessage(this::onMessage);
                websocket.onClose(this::onClose);
                websocket.onError(this::onError);
            });
        });
        application.start();
        gameLoop.start(this::tick);

        System.out.println(
                "Game server listening on ws://" + host + ":" + application.port()
                        + GameProtocol.WEBSOCKET_PATH
        );
    }

    private void onConnect(WsContext connection) {
        clients.register(connection).ifPresentOrElse(registration -> {
            world.addPlayer(registration.playerId());
            publishCurrentSnapshot(true);
            System.out.println(
                    registration.playerId() + " connected ("
                            + registration.connectedClients() + "/" + GameProtocol.MAX_PLAYERS + ")"
            );
        }, () -> connection.closeSession(1013, "Server is full"));
    }

    private void onMessage(WsMessageContext connection) {
        clients.playerId(connection).ifPresent(playerId -> {
            try {
                PlayerInput input = jsonCodec.decode(connection.message(), PlayerInput.class);
                world.acceptInput(playerId, input);
            } catch (RuntimeException exception) {
                System.err.println("Rejected client message: " + exception.getMessage());
            }
        });
    }

    private void onClose(WsCloseContext connection) {
        removeClient(connection);
    }

    private void onError(WsErrorContext connection) {
        removeClient(connection);
        System.err.println("WebSocket error: " + connection.error().getMessage());
    }

    private void removeClient(WsContext connection) {
        clients.unregister(connection).ifPresent(playerId -> {
            world.removePlayer(playerId);
            publishCurrentSnapshot(true);
            System.out.println(
                    playerId + " disconnected (" + clients.size() + "/" + GameProtocol.MAX_PLAYERS + ")"
            );
        });
    }

    private void tick() {
        GameSnapshot snapshot = world.tick();
        publishSnapshot(snapshot, false);
    }

    private void publishCurrentSnapshot(boolean force) {
        publishSnapshot(world.snapshot(), force);
    }

    private synchronized void publishSnapshot(GameSnapshot snapshot, boolean force) {
        if (!force && snapshot.equals(lastPublishedSnapshot)) {
            return;
        }

        String json = jsonCodec.encode(snapshot);
        for (WsContext connection : clients.connections()) {
            try {
                connection.send(json);
            } catch (RuntimeException exception) {
                System.err.println("Could not publish snapshot: " + exception.getMessage());
            }
        }
        lastPublishedSnapshot = snapshot;
    }

    @Override
    public synchronized void close() {
        gameLoop.close();
        if (application != null) {
            application.stop();
            application = null;
        }
    }
}
