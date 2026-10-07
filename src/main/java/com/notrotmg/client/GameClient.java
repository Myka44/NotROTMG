package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.protocol.clienttoserver.ClientCommand;
import com.notrotmg.protocol.servertoclient.ServerMessage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Consumer;

/** Minimal WebSocket client used by the JavaFX application. */
public final class GameClient implements AutoCloseable {
    private final URI serverUri;
    private final JacksonJsonCodec jsonCodec;
    private final Consumer<ServerMessage> messageHandler;
    private final Consumer<String> statusHandler;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private CompletableFuture<WebSocket> connection;
    private CompletableFuture<?> sendTail = CompletableFuture.completedFuture(null);
    private volatile WebSocket webSocket;

    public GameClient(
            URI serverUri,
            JacksonJsonCodec jsonCodec,
            Consumer<ServerMessage> messageHandler,
            Consumer<String> statusHandler
    ) {
        this.serverUri = Objects.requireNonNull(serverUri);
        this.jsonCodec = Objects.requireNonNull(jsonCodec);
        this.messageHandler = Objects.requireNonNull(messageHandler);
        this.statusHandler = Objects.requireNonNull(statusHandler);
    }

    public void connect() {
        connection = httpClient.newWebSocketBuilder()
                .buildAsync(serverUri, new SocketListener());
        connection.whenComplete((socket, error) -> {
            if (error != null) {
                statusHandler.accept("Connection error: " + messageOf(error));
            }
        });
    }

    public synchronized void send(ClientCommand command) {
        WebSocket socket = webSocket;
        if (socket == null) {
            return;
        }
        String json = jsonCodec.encode(command);
        sendTail = sendTail
                .exceptionally(error -> null)
                .thenCompose(ignored -> socket.sendText(json, true));
    }

    @Override
    public void close() {
        WebSocket socket = webSocket;
        webSocket = null;
        if (socket != null) {
            socket.sendClose(WebSocket.NORMAL_CLOSURE, "Client closed");
        } else if (connection != null) {
            connection.cancel(true);
        }
    }

    private static String messageOf(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause.getMessage() == null ? cause.getClass().getSimpleName() : cause.getMessage();
    }

    private final class SocketListener implements WebSocket.Listener {
        private final StringBuilder partialMessage = new StringBuilder();

        @Override
        public void onOpen(WebSocket socket) {
            webSocket = socket;
            statusHandler.accept("Connected");
            socket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket socket, CharSequence data, boolean last) {
            partialMessage.append(data);
            if (last) {
                String json = partialMessage.toString();
                partialMessage.setLength(0);
                try {
                    ServerMessage message = jsonCodec.decode(json, ServerMessage.class);
                    messageHandler.accept(message);
                } catch (RuntimeException error) {
                    statusHandler.accept("Invalid server message: " + messageOf(error));
                }
            }
            socket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket socket, int statusCode, String reason) {
            webSocket = null;
            statusHandler.accept("Disconnected (" + statusCode + ")");
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket socket, Throwable error) {
            webSocket = null;
            statusHandler.accept("Connection error: " + messageOf(error));
        }
    }
}
