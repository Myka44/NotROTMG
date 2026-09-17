package com.notrotmg.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private final Connection connection;

    private DatabaseManager(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
        initializeSchema();
    }

    public static DatabaseManager getInstance(String url) throws SQLException {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager(url);
                }
            }
        }
        return instance;
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DatabaseManager not initialized. Call getInstance(String url) first.");
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS enemies (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    type INTEGER NOT NULL,
                    hp INTEGER NOT NULL
                )
            """);
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS map_elements (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    type INTEGER NOT NULL
                )
            """);
            seedData(stmt);
        }
    }

    private void seedData(Statement stmt) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM enemies");
        if (rs.next() && rs.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO enemies (name, x, y, type, hp) VALUES ('Slime', 200, 200, 0, 20)");
            stmt.executeUpdate("INSERT INTO enemies (name, x, y, type, hp) VALUES ('Skeleton', 400, 300, 1, 40)");
            stmt.executeUpdate("INSERT INTO enemies (name, x, y, type, hp) VALUES ('Ghost', 600, 150, 2, 30)");
        }
        rs.close();
        ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM map_elements");
        if (rs2.next() && rs2.getInt(1) == 0) {
            stmt.executeUpdate("INSERT INTO map_elements (x, y, type) VALUES (100, 100, 0)");
            stmt.executeUpdate("INSERT INTO map_elements (x, y, type) VALUES (500, 400, 1)");
            stmt.executeUpdate("INSERT INTO map_elements (x, y, type) VALUES (700, 200, 2)");
        }
        rs2.close();
    }
}
