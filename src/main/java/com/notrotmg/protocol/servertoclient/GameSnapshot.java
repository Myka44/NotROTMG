package com.notrotmg.protocol.servertoclient;

import java.util.List;

/** Complete authoritative world snapshot sent to a connected client. */
public record GameSnapshot(
        int canvasWidth,
        int canvasHeight,
        List<PlayerSnapshot> players,
        List<EnemySnapshot> enemies,
        List<ProjectileSnapshot> projectiles,
        List<MapObjectSnapshot> mapObjects,
        int worldWidth,
        int worldHeight,
        String controlledPlayerId
) implements Snapshot {
    public GameSnapshot {
        players = List.copyOf(players);
        enemies = List.copyOf(enemies);
        projectiles = List.copyOf(projectiles);
        mapObjects = List.copyOf(mapObjects);
    }

    public GameSnapshot forPlayer(String playerId) {
        return new GameSnapshot(
                canvasWidth,
                canvasHeight,
                players,
                enemies,
                projectiles,
                mapObjects,
                worldWidth,
                worldHeight,
                playerId
        );
    }
}
