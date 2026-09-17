package com.notrotmg.notrotmg;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/** Small JDK WebSocket client used by the JavaFX application. */
public final class GameWebSocketClient implements AutoCloseable {
    public static final URI LOCAL_SERVER_URI = URI.create("ws://127.0.0.1:7070/game");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Consumer<String> statusHandler;
    private CompletableFuture<WebSocket> connection;
    private CompletableFuture<?> sendTail = CompletableFuture.completedFuture(null);
    private WebSocket webSocket;

    public GameWebSocketClient(Consumer<GameState> stateHandler, Consumer<String> statusHandler) {
        this.statusHandler = statusHandler;
        ClientGameListener listener = new ClientGameListener(objectMapper, stateHandler, statusHandler);

        statusHandler.accept("Connecting to " + LOCAL_SERVER_URI + " ...");
        connection = httpClient.newWebSocketBuilder()
                .buildAsync(LOCAL_SERVER_URI, listener)
                .whenComplete((socket, error) -> {
                    if (error != null) {
                        statusHandler.accept("Server unavailable: " + rootMessage(error));
                    } else {
                        webSocket = socket;
                    }
                });
    }

    public synchronized void sendInput(int dx, int dy) {
        if (webSocket == null || webSocket.isOutputClosed()) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(new MoveCommand(dx, dy));
            sendTail = sendTail.handle((ignored, error) -> null)
                    .thenCompose(ignored -> webSocket.sendText(json, true));
        } catch (Exception exception) {
            statusHandler.accept("Could not send input: " + exception.getMessage());
        }
    }

    @Override
    public synchronized void close() {
        if (webSocket != null && !webSocket.isOutputClosed()) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Client closed");
        } else if (connection != null) {
            connection.cancel(true);
        }
    }

    private static String rootMessage(Throwable error) {
        Throwable root = error;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return root.getMessage();
    }
}
