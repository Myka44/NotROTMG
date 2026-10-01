package com.notrotmg.server;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Player;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.WorldBounds;
import com.notrotmg.domain.item.ArcherItemFactory;
import com.notrotmg.domain.item.CharacterItemFactory;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.WarriorItemFactory;
import com.notrotmg.domain.item.WizardItemFactory;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
    private final SnapshotMapper snapshotMapper;

    public AuthoritativeGameWorld() {
        this(new SnapshotMapper());
    }

    public AuthoritativeGameWorld(SnapshotMapper snapshotMapper) {
        this.snapshotMapper = Objects.requireNonNull(snapshotMapper);
    }

    public synchronized GameSnapshot addPlayer(String playerId) {
        if (!players.containsKey(playerId)) {
            int spawnSlot = firstAvailableSpawnSlot();
            int[] spawn = SPAWN_POSITIONS[spawnSlot];
            Player player = new Player(
                    playerId,
                    spawnSlot,
                    new Position(spawn[0], spawn[1]),
                    CharacterClass.WARRIOR
            );
            addStartingItems(player);
            players.put(playerId, player);
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

    public synchronized void equipItem(String playerId, int inventorySlot) {
        requirePlayer(playerId).equipFromInventorySlot(inventorySlot);
    }

    public synchronized boolean unequipItem(String playerId, EquipmentSlot equipmentSlot) {
        return requirePlayer(playerId).unequip(equipmentSlot);
    }

    public synchronized boolean dropItem(String playerId, int inventorySlot) {
        return requirePlayer(playerId).dropFromInventorySlot(inventorySlot);
    }

    public synchronized Optional<InventorySnapshot> inventorySnapshot(String playerId) {
        Player player = players.get(playerId);
        return player == null
                ? Optional.empty()
                : Optional.of(snapshotMapper.inventorySnapshot(player.inventory()));
    }

    public synchronized GameSnapshot tick() {
        players.values().forEach(player -> player.move(GameRules.FIXED_DELTA_SECONDS, bounds));
        return snapshot();
    }

    public synchronized GameSnapshot snapshot() {
        return snapshotMapper.gameSnapshot(players.values());
    }

    private void addStartingItems(Player player) {
        CharacterItemFactory itemFactory = itemFactoryFor(player.characterClass());
        player.addItem(itemFactory.createWeapon());
        player.addItem(itemFactory.createArmor());
        player.addItem(itemFactory.createAbility());
        player.addItem(itemFactory.createRing());
    }

    private CharacterItemFactory itemFactoryFor(CharacterClass characterClass) {
        return switch (characterClass) {
            case WARRIOR -> new WarriorItemFactory();
            case WIZARD -> new WizardItemFactory();
            case ARCHER -> new ArcherItemFactory();
        };
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

    private Player requirePlayer(String playerId) {
        Player player = players.get(playerId);
        if (player == null) {
            throw new IllegalArgumentException("Unknown player: " + playerId);
        }
        return player;
    }
}
