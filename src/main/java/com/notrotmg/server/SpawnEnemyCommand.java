package com.notrotmg.server;

import com.notrotmg.domain.enemy.Enemy;

import java.util.Objects;

/**
 * Concrete command that spawns one enemy and despawns exactly that enemy when
 * undone. The receiver is the authoritative world.
 */
public final class SpawnEnemyCommand implements GameCommand {
    private final AuthoritativeGameWorld world;
    private final Enemy enemy;
    private boolean spawned;

    public SpawnEnemyCommand(AuthoritativeGameWorld world, Enemy enemy) {
        this.world = Objects.requireNonNull(world);
        this.enemy = Objects.requireNonNull(enemy);
    }

    @Override
    public void execute() {
        if (spawned) {
            return;
        }
        if (!world.spawn(enemy)) {
            throw new IllegalStateException("Enemy already present in world: " + enemy.id());
        }
        spawned = true;
    }

    @Override
    public void undo() {
        if (!spawned) {
            return;
        }
        world.despawn(enemy.id());
        spawned = false;
    }

    public String enemyId() {
        return enemy.id();
    }
}
