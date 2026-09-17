package com.notrotmg.database;

import com.notrotmg.server.Enemy;
import com.notrotmg.server.MapElement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityFactory {
    public static Enemy createEnemy(ResultSet rs) throws SQLException {
        return new Enemy(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getInt("x"),
            rs.getInt("y"),
            rs.getInt("type"),
            rs.getInt("hp")
        );
    }

    public static MapElement createMapElement(ResultSet rs) throws SQLException {
        return new MapElement(
            rs.getInt("id"),
            rs.getInt("x"),
            rs.getInt("y"),
            rs.getInt("type")
        );
    }
}
