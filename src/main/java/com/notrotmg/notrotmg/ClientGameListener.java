package com.notrotmg.notrotmg;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/** Converts WebSocket JSON messages into game-state updates for the JavaFX client. */
public final class ClientGameListener implements WebSocket.Listener {
    private final ObjectMapper objectMapper;
    private final Consumer<GameState> stateHandler;
    private final Consumer<String> statusHandler;
    private final StringBuilder partialMessage = new StringBuilder();

    public ClientGameListener(
            ObjectMapper objectMapper,
            Consumer<GameState> stateHandler,
            Consumer<String> statusHandler
    ) {
        this.objectMapper = objectMapper;
        this.stateHandler = stateHandler;
        this.statusHandler = statusHandler;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        statusHandler.accept("Connected");
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        partialMessage.append(data);

        if (last) {
            String message = partialMessage.toString();
            partialMessage.setLength(0);
            try {
                stateHandler.accept(objectMapper.readValue(message, GameState.class));
            } catch (Exception exception) {
                statusHandler.accept("Invalid server message: " + exception.getMessage());
            }
        }

        webSocket.request(1);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        statusHandler.accept("Disconnected (" + statusCode + ")");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        statusHandler.accept("Connection error: " + error.getMessage());
    }
}
