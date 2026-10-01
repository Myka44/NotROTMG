package com.notrotmg.server;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Player;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.WorldBounds;
import com.notrotmg.protocol.GameSnapshot;
import com.notrotmg.protocol.PlayerInput;
import com.notrotmg.protocol.PlayerSnapshot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Application service that owns and updates all authoritative domain entities. */
public final class AuthoritativeGameWorld {
    private static final int[][] SPAWN_POSITIONS = {
            {64, 64},
            {GameRules.CANVAS_WIDTH - GameRules.PLAYER_SIZE - 64, 64},
            {64, GameRules.CANVAS_HEIGHT - GameRules.PLAYER_SIZE - 64},
            {
                    GameRules.CANVAS_WIDTH - GameRules.PLAYER_SIZE - 64,
                    GameRules.CANVAS_HEIGHT - GameRules.PLAYER_SIZE - 64
            }
    };

    private final WorldBounds bounds = new WorldBounds(
            GameRules.CANVAS_WIDTH,
            GameRules.CANVAS_HEIGHT
    );
    private final Map<String, Player> players = new LinkedHashMap<>();

    public synchronized GameSnapshot addPlayer(String playerId) {
        if (!players.containsKey(playerId)) {
            int spawnSlot = firstAvailableSpawnSlot();
            int[] spawn = SPAWN_POSITIONS[spawnSlot];
            players.put(
                    playerId,
                    new Player(
                            playerId,
                            spawnSlot,
                            new Position(spawn[0], spawn[1]),
                            CharacterClass.WARRIOR
                    )
            );
        }
        return snapshot();
    }

    public synchronized GameSnapshot removePlayer(String playerId) {
        players.remove(playerId);
        return snapshot();
    }

    public synchronized void acceptInput(String playerId, PlayerInput input) {
        Player player = players.get(playerId);
        if (player != null) {
            player.movementDirection(new Vector2(input.horizontal(), input.vertical()));
        }
    }

    public synchronized GameSnapshot tick() {
        players.values().forEach(player -> player.move(GameRules.FIXED_DELTA_SECONDS, bounds));
        return snapshot();
    }

    public synchronized GameSnapshot snapshot() {
        List<PlayerSnapshot> playerSnapshots = players.values().stream()
                .map(this::snapshotOf)
                .toList();
        return new GameSnapshot(
                GameRules.CANVAS_WIDTH,
                GameRules.CANVAS_HEIGHT,
                playerSnapshots
        );
    }

    private PlayerSnapshot snapshotOf(Player player) {
        return new PlayerSnapshot(
                player.id(),
                (int) Math.round(player.position().x()),
                (int) Math.round(player.position().y()),
                (int) Math.round(player.size()),
                player.level(),
                player.currentHealth(),
                player.stats().maxHealth(),
                player.characterClass().name()
        );
    }

    private int firstAvailableSpawnSlot() {
        for (int slot = 0; slot < SPAWN_POSITIONS.length; slot++) {
            int candidate = slot;
            boolean occupied = players.values().stream()
                    .anyMatch(player -> player.spawnSlot() == candidate);
            if (!occupied) {
                return slot;
            }
        }
        throw new IllegalStateException("No player spawn slot is available");
    }
}
