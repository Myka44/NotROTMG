package com.notrotmg.server;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.Entity;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Player;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.WorldBounds;
import com.notrotmg.domain.enemy.Enemy;
import com.notrotmg.domain.enemy.EnemyFactory;
import com.notrotmg.domain.enemy.EnemyType;
import com.notrotmg.domain.item.ArcherItemFactory;
import com.notrotmg.domain.item.CharacterItemFactory;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.WarriorItemFactory;
import com.notrotmg.domain.item.WizardItemFactory;
import com.notrotmg.domain.projectile.Projectile;
import com.notrotmg.domain.projectile.ProjectileSide;
import com.notrotmg.domain.map.MapFeature;
import com.notrotmg.domain.map.WorldMapSetup;
import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/** Application service that owns and updates all authoritative domain entities. */
public final class AuthoritativeGameWorld {
    private static final int STARTING_WEAPON_INVENTORY_SLOT = 0;
    private static final int[][] SPAWN_POSITIONS = {
            {900, 560},
            {1000, 560},
            {900, 660},
            {1000, 660}
    };

    private final WorldBounds bounds = new WorldBounds(
            GameRules.WORLD_WIDTH,
            GameRules.WORLD_HEIGHT
    );
    private final MapFeature map = WorldMapSetup.createWorld();
    
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final Map<String, Enemy> enemies = new LinkedHashMap<>();
    private final Map<String, Projectile> projectiles = new LinkedHashMap<>();
    private final SnapshotMapper snapshotMapper;

    public AuthoritativeGameWorld() {
        this(new SnapshotMapper());
    }

    public AuthoritativeGameWorld(SnapshotMapper snapshotMapper) {
        this.snapshotMapper = Objects.requireNonNull(snapshotMapper);
        enemies.put("slime-1", EnemyFactory.create(
                EnemyType.REGULAR, "slime-1", new Position(220, 170)
        ));
        enemies.put("boss-1", EnemyFactory.create(
                EnemyType.BOSS, "boss-1", new Position(400, 260)
        ));
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

    public synchronized void acceptAttackInput(String playerId, AttackInput input) {
        Player player = players.get(playerId);
        if (player != null) {
            player.attackInput(new Position(input.targetX(), input.targetY()), input.firing());
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
        players.values().stream()
                .flatMap(player -> player.updateAttack(GameRules.FIXED_DELTA_SECONDS).stream())
                .forEach(projectile -> projectiles.put(projectile.id(), projectile));
        for (Enemy enemy : enemies.values()) {
            Player target = nearestLivingPlayer(enemy);
            if (target != null) {
                enemy.update(target.center(), GameRules.FIXED_DELTA_SECONDS, bounds)
                        .forEach(projectile -> projectiles.put(projectile.id(), projectile));
            }
        }

        Iterator<Projectile> iterator = projectiles.values().iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            Position previousPosition = projectile.position();
            projectile.update(GameRules.FIXED_DELTA_SECONDS);

            Entity target = firstHitTarget(projectile, previousPosition);
            if (target != null) {
                target.takeDamage(projectile.damage());
                iterator.remove();
            } else if (projectile.isExpired() || isOutsideWorld(projectile)) {
                iterator.remove();
            }
        }
        enemies.values().removeIf(enemy -> !enemy.isAlive());
        return snapshot();
    }

    public synchronized GameSnapshot snapshot() {
        return snapshotMapper.gameSnapshot(players.values(), enemies.values(), projectiles.values(), map);
    }

    private Player nearestLivingPlayer(Enemy enemy) {
        Position origin = enemy.center();
        return players.values().stream()
                .filter(Player::isAlive)
                .min(Comparator.comparingDouble(player -> squaredDistance(origin, player.center())))
                .orElse(null);
    }

    private static double squaredDistance(Position first, Position second) {
        double dx = first.x() - second.x();
        double dy = first.y() - second.y();
        return dx * dx + dy * dy;
    }

    private Entity firstHitTarget(Projectile projectile, Position previousPosition) {
        Iterable<? extends Entity> targets = projectile.side() == ProjectileSide.PLAYER
                ? enemies.values()
                : players.values();
        Entity firstTarget = null;
        double firstFraction = Double.POSITIVE_INFINITY;
        for (Entity target : targets) {
            if (!target.isAlive()) {
                continue;
            }
            double fraction = ProjectileCollision.hitFraction(projectile, previousPosition, target);
            if (fraction < firstFraction) {
                firstFraction = fraction;
                firstTarget = target;
            }
        }
        return firstTarget;
    }

    private boolean isOutsideWorld(Projectile projectile) {
        double margin = projectile.length();
        Position position = projectile.position();
        return position.x() < -margin
                || position.y() < -margin
                || position.x() > bounds.width() + margin
                || position.y() > bounds.height() + margin;
    }

    private void addStartingItems(Player player) {
        CharacterItemFactory itemFactory = itemFactoryFor(player.characterClass());
        player.addItem(itemFactory.createWeapon());
        player.addItem(itemFactory.createArmor());
        player.addItem(itemFactory.createAbility());
        player.addItem(itemFactory.createRing());
        player.equipFromInventorySlot(STARTING_WEAPON_INVENTORY_SLOT);
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
