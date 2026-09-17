package com.notrotmg.server;

import com.notrotmg.database.DatabaseManager;
import com.notrotmg.database.EnemyDAO;
import com.notrotmg.database.MapElementDAO;

import java.sql.SQLException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class GameServer {
    private static final String DB_URL = "jdbc:sqlite:notrotmg.db";
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try {
            DatabaseManager db = DatabaseManager.getInstance(DB_URL);
            EnemyDAO enemyDAO = new EnemyDAO(db.getConnection());
            MapElementDAO mapElementDAO = new MapElementDAO(db.getConnection());

            GameState gameState = GameState.getInstance();
            gameState.setEnemies(enemyDAO.findAll());
            gameState.setMapElements(mapElementDAO.findAll());

            Server server = new Server(PORT);
            ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
            context.setContextPath("/");
            context.addServlet(new ServletHolder(new GameEndpoint.GameWebSocketServlet()), "/game/*");
            server.setHandler(context);
            server.start();
            gameState.start();
            System.out.println("[STATE] gameState started");

            String host = ((ServerConnector) server.getConnectors()[0]).getHost();
            System.out.println("GameServer started on ws://" + (host != null ? host : "0.0.0.0") + ":" + PORT + "/game");
            System.out.println("Press Enter to stop...");
            System.in.read();

            gameState.halt();
            server.stop();
            System.out.println("GameServer stopped.");
        } catch (Exception e) {
            System.err.println("Failed to start GameServer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
