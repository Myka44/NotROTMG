package com.notrotmg.protocol.servertoclient;

import java.util.List;

/** Complete authoritative world snapshot sent to every client. */
public record GameSnapshot(
        int canvasWidth,
        int canvasHeight,
        List<PlayerSnapshot> players,
        List<EnemySnapshot> enemies,
        List<ProjectileSnapshot> projectiles
) implements Snapshot {
    public GameSnapshot {
        players = List.copyOf(players);
        enemies = List.copyOf(enemies);
        projectiles = List.copyOf(projectiles);
    }
}
