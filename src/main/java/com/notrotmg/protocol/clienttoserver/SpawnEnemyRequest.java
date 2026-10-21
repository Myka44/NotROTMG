package com.notrotmg.protocol.clienttoserver;

/** Debug request that spawns a regular enemy at the given world position. */
public record SpawnEnemyRequest(double x, double y) implements ClientCommand {
    public SpawnEnemyRequest {
        if (!Double.isFinite(x) || !Double.isFinite(y)) {
            throw new IllegalArgumentException("Spawn position must be finite");
        }
    }
}
