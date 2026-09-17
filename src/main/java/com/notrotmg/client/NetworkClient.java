package com.notrotmg.client;

import com.notrotmg.shared.StateSnapshot;
import com.notrotmg.shared.Message;
import com.notrotmg.shared.PlayerJoin;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class NetworkClient {
    private final GamePanel gamePanel;
    private volatile Session session;
    private WebSocketClient client;
    private String playerName;
    private String playerId;
    private CountDownLatch connectionLatch;
    private CountDownLatch closeLatch;
    private boolean joined = false;
    private volatile boolean running = true;

    public NetworkClient(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isJoined() {
        return joined;
    }

    public void connect(String serverUri, String playerName) {
        this.playerId = "player-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 10000);
        this.playerName = playerName;
        this.connectionLatch = new CountDownLatch(1);
        this.closeLatch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                client = new WebSocketClient();
                client.start();
                WebSocketAdapter adapter = new WebSocketAdapter() {
                    @Override
                    public void onWebSocketConnect(Session session) {
                        System.out.println("[CLIENT] Connected");
                        NetworkClient.this.session = session;
                        String joinMessage = new com.notrotmg.shared.PlayerJoin(playerId, playerName, (int)(Math.random() * 0xFFFFFF)).toJson();
                        System.out.println("[CLIENT] Sending join: " + joinMessage);
                        try {
                            session.getRemote().sendString(joinMessage);
                            System.out.println("[CLIENT] Join sent");
                        } catch (IOException e) {
                            System.err.println("[CLIENT] Failed to send join: " + e.getMessage());
                        }
                    }

                    @Override
                    public void onWebSocketText(String message) {
                        System.out.println("[CLIENT] Received: " + message);
                        Message msg = Message.fromJson(message);
                        if (msg instanceof PlayerJoin join) {
                            joined = true;
                            gamePanel.setLocalPlayerId(playerId);
                            gamePanel.setLocalColor(join.color);
                            System.out.println("[CLIENT] Join confirmed by server, localPlayerId=" + playerId);
                            return;
                        }
                        if (msg instanceof StateSnapshot snapshot) {
                            System.out.println("[CLIENT] Snapshot players=" + snapshot.players.size()
                                + " enemies=" + snapshot.enemies.size()
                                + " elements=" + snapshot.mapElements.size());
                            gamePanel.mergeState(snapshot, playerId, joined);
                        }
                    }

                    @Override
                    public void onWebSocketClose(int statusCode, String reason) {
                        System.out.println("[CLIENT] Disconnected: " + reason);
                        running = false;
                        closeLatch.countDown();
                    }

                    @Override
                    public void onWebSocketError(Throwable cause) {
                        System.err.println("[CLIENT] WebSocket error: " + cause.getMessage());
                        cause.printStackTrace();
                        running = false;
                        closeLatch.countDown();
                    }
                };
                client.connect(adapter, URI.create(serverUri));
                connectionLatch.countDown();
                System.out.println("[CLIENT] Waiting for close...");
                closeLatch.await(10, TimeUnit.MINUTES);
            } catch (Exception e) {
                System.err.println("[CLIENT] Failed: " + e.getMessage());
                e.printStackTrace();
                connectionLatch.countDown();
                closeLatch.countDown();
            }
        }).start();
    }

    public void send(String message) {
        Session s = session;
        if (s != null && s.isOpen()) {
            try {
                s.getRemote().sendString(message);
            } catch (IOException e) {
                System.err.println("[CLIENT] Failed to send: " + e.getMessage());
            }
        }
    }

    public void disconnect() {
        running = false;
        Session s = session;
        if (s != null) {
            try {
                s.disconnect();
            } catch (IOException e) {
                System.err.println("[CLIENT] Failed to disconnect: " + e.getMessage());
            }
        }
    }
}
