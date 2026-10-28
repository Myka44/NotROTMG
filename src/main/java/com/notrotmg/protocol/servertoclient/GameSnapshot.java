package com.notrotmg.protocol.servertoclient;

import java.util.List;

/** Complete authoritative world snapshot sent to every client. */
public record GameSnapshot(
    int worldWidth,
    int worldHeight,
        List<PlayerSnapshot> players,
        List<EnemySnapshot> enemies,
    List<ProjectileSnapshot> projectiles,
    String followedPlayerId
) implements Snapshot {
    public GameSnapshot {
        players = List.copyOf(players);
        enemies = List.copyOf(enemies);
        projectiles = List.copyOf(projectiles);
    }

    public GameSnapshot forPlayer(String playerId) {
        return new GameSnapshot(worldWidth, worldHeight, players, enemies, projectiles, playerId);
    }
}
