package com.notrotmg.database;

import com.notrotmg.server.MapElement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MapElementDAO {
    private final Connection connection;

    public MapElementDAO(Connection connection) {
        this.connection = connection;
    }

    public List<MapElement> findAll() throws SQLException {
        List<MapElement> elements = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, x, y, type FROM map_elements");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                elements.add(new MapElement(
                    rs.getInt("id"),
                    rs.getInt("x"),
                    rs.getInt("y"),
                    rs.getInt("type")
                ));
            }
        }
        return elements;
    }
}
