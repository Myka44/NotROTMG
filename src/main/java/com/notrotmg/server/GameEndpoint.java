package com.notrotmg.server;

import com.notrotmg.shared.Message;
import com.notrotmg.shared.StateSnapshot;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class GameEndpoint extends WebSocketAdapter {
    private static final Set<Session> sessions = new CopyOnWriteArraySet<>();
    private static final java.util.Map<Session, String> sessionToPlayerId = new java.util.concurrent.ConcurrentHashMap<>();
    private Session session;

    @Override
    public void onWebSocketConnect(Session session) {
        this.session = session;
        sessions.add(session);
        sessionToPlayerId.put(session, "unknown");
        System.out.println("[SERVER] Client connected, sessions=" + sessions.size());
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("[SERVER] Received text: " + message);
        Message msg;
        try {
            msg = Message.fromJson(message);
        } catch (Exception e) {
            System.err.println("[SERVER] fromJson threw: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        if (msg == null) {
            System.out.println("[SERVER] fromJson returned null");
            return;
        }
        System.out.println("[SERVER] Parsed type=" + msg.getType() + " class=" + msg.getClass().getName());

        Session session = this.session;
        System.out.println("[SERVER] sessionFromField=" + session + " open=" + (session != null && session.isOpen()));
        if (session == null || !session.isOpen()) return;

        try {
            String type = msg.getType();
            System.out.println("[SERVER] type='" + type + "'");
            if ("player_join".equals(type)) {
                System.out.println("[SERVER] Entered player_join branch");
                String playerId = null;
                String playerName = null;
                int color = 0;
                if (msg instanceof com.notrotmg.shared.PlayerJoin join) {
                    playerId = join.playerId;
                    playerName = join.playerName;
                    color = join.color;
                } else {
                    System.out.println("[SERVER] player_join message is not PlayerJoin instance, extracting fields via Gson");
                    com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(message).getAsJsonObject();
                    playerId = obj.get("playerId").getAsString();
                    playerName = obj.get("playerName").getAsString();
                    color = obj.get("color").getAsInt();
                }
                if (playerId == null || playerId.isEmpty()) {
                    playerId = String.valueOf(session.hashCode());
                }
                System.out.println("[SERVER] PlayerJoin id=" + playerId + " name=" + playerName);
                Player player = new Player(playerId, playerName, color, 100, 100, 100);
                GameState.getInstance().addPlayer(player);
                sessionToPlayerId.put(session, playerId);
                System.out.println("[SERVER] Players count=" + GameState.getInstance().getPlayers().size());
                String response = new com.notrotmg.shared.PlayerJoin(playerId, playerName, color).toJson();
                sendToSession(session, response);
            } else if ("input".equals(type)) {
                String playerId = null;
                int dx = 0;
                int dy = 0;
                if (msg instanceof com.notrotmg.shared.PlayerInput input) {
                    playerId = input.playerId();
                    dx = input.dx();
                    dy = input.dy();
                } else {
                    com.google.gson.JsonObject obj = com.google.gson.JsonParser.parseString(message).getAsJsonObject();
                    playerId = obj.get("playerId").getAsString();
                    dx = obj.get("dx").getAsInt();
                    dy = obj.get("dy").getAsInt();
                }
                GameLoop loop = GameState.getInstance().getGameLoop();
                if (loop != null) {
                    loop.enqueueInput(new com.notrotmg.shared.PlayerInput(playerId, dx, dy));
                }
            }
        } catch (Throwable t) {
            System.err.println("[SERVER] Handler threw: " + t.getMessage());
            t.printStackTrace();
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        Session session = this.session;
        sessions.remove(session);
        if (session != null) {
            String playerId = sessionToPlayerId.remove(session);
            if (playerId != null) {
                GameState.getInstance().removePlayer(playerId);
                System.out.println("[SERVER] Removed player: " + playerId);
            }
        }
        this.session = null;
        System.out.println("[SERVER] Client closed: " + reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        Session session = this.session;
        sessions.remove(session);
        if (session != null) {
            String playerId = sessionToPlayerId.remove(session);
            if (playerId != null) {
                GameState.getInstance().removePlayer(playerId);
                System.out.println("[SERVER] Removed player on error: " + playerId);
            }
        }
        this.session = null;
        System.out.println("[SERVER] WebSocket error: " + cause.getMessage());
        cause.printStackTrace();
    }

    public static void broadcast(Message message) {
        String json = message.toJson();
        System.out.println("[SERVER] Broadcasting players=" +
            ((StateSnapshot) message).players.size() + " " + json);
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getRemote().sendString(json);
                } catch (IOException e) {
                    System.err.println("[SERVER] Send failed: " + e.getMessage());
                }
            }
        }
    }

    private static void sendToSession(Session session, String message) {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) {
            System.err.println("[SERVER] Send to session failed: " + e.getMessage());
        }
    }

    public static class GameWebSocketServlet extends WebSocketServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.getWriter().println("NotROTMG WebSocket endpoint. Connect via ws://" + req.getServerName() + ":" + req.getServerPort() + req.getRequestURI());
        }

        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.register(GameEndpoint.class);
        }
    }
}
