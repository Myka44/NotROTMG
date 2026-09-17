package com.notrotmg.database;

import com.notrotmg.server.Enemy;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnemyDAO {
    private final Connection connection;

    public EnemyDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Enemy> findAll() throws SQLException {
        List<Enemy> enemies = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name, x, y, type, hp FROM enemies");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                enemies.add(new Enemy(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("x"),
                    rs.getInt("y"),
                    rs.getInt("type"),
                    rs.getInt("hp")
                ));
            }
        }
        return enemies;
    }

    public Enemy findById(int id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name, x, y, type, hp FROM enemies WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Enemy(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("type"),
                        rs.getInt("hp")
                    );
                }
            }
        }
        return null;
    }
}
