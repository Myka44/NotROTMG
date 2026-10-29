package com.notrotmg.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import com.notrotmg.common.logging.GameLogger;
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
import com.notrotmg.protocol.clienttoserver.AttackInput;
import com.notrotmg.protocol.clienttoserver.PlayerInput;
import com.notrotmg.protocol.servertoclient.CombatEvent;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;

/** Application service that owns and updates all authoritative domain entities. */
public final class AuthoritativeGameWorld {
    private static final int STARTING_WEAPON_INVENTORY_SLOT = 0;
    private static final int[][] SPAWN_POSITIONS = {
            {64, 64},
            {GameRules.WORLD_WIDTH - GameRules.PLAYER_SIZE - 64, 64},
            {64, GameRules.WORLD_HEIGHT - GameRules.PLAYER_SIZE - 64},
            {
                GameRules.WORLD_WIDTH - GameRules.PLAYER_SIZE - 64,
                GameRules.WORLD_HEIGHT - GameRules.PLAYER_SIZE - 64
            }
    };

    private final WorldBounds bounds = new WorldBounds(
            GameRules.WORLD_WIDTH,
            GameRules.WORLD_HEIGHT
    );
    private final Map<String, Player> players = new LinkedHashMap<>();
    private final Map<String, Enemy> enemies = new LinkedHashMap<>();
    private final Map<String, Projectile> projectiles = new LinkedHashMap<>();
    private final List<CombatEvent> combatEvents = new ArrayList<>();
    private final SnapshotMapper snapshotMapper;
    private final CommandInvoker commandInvoker = new CommandInvoker();
    private final EnemySpawner enemySpawner = new EnemySpawner();
    private final GameLogger logger = GameLogger.getInstance();
    private long nextDebugSpawnId = 1;

    public AuthoritativeGameWorld() {
        this(new SnapshotMapper());
    }

    public AuthoritativeGameWorld(SnapshotMapper snapshotMapper) {
        this.snapshotMapper = Objects.requireNonNull(snapshotMapper);
        commandInvoker.execute(new SpawnEnemyCommand(
                this,
                EnemyFactory.create(EnemyType.REGULAR, "slime-1", new Position(220, 170))
        ));
        commandInvoker.execute(new SpawnEnemyCommand(
                this,
                EnemyFactory.create(EnemyType.BOSS, "boss-1", new Position(400, 260))
        ));
    }

    public synchronized GameSnapshot addPlayer(String playerId, CharacterClass characterClass) {
        Objects.requireNonNull(characterClass);
        if (!players.containsKey(playerId)) {
            int spawnSlot = firstAvailableSpawnSlot();
            int[] spawn = SPAWN_POSITIONS[spawnSlot];
            Player player = new Player(
                    playerId,
                    spawnSlot,
                    new Position(spawn[0], spawn[1]),
                    characterClass
            );
            addStartingItems(player);
            players.put(playerId, player);
        }
        return snapshot();
    }

    private CharacterClass _randomClass() {
    List<CharacterClass> classes = new ArrayList<>(List.of(
        CharacterClass.WARRIOR,
        CharacterClass.ARCHER,
        CharacterClass.WIZARD
    ));

    Collections.shuffle(classes, ThreadLocalRandom.current());
    return classes.get(0);
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
        combatEvents.clear();
        players.values().forEach(player -> player.move(GameRules.FIXED_DELTA_SECONDS, bounds));
        players.values().forEach(player -> addAttack(player.updateAttack(GameRules.FIXED_DELTA_SECONDS)));
        for (Enemy enemy : enemies.values()) {
            Player target = nearestLivingPlayer(enemy);
            if (target != null) {
                addAttack(enemy.update(target.center(), GameRules.FIXED_DELTA_SECONDS, bounds));
            }
        }

        Iterator<Projectile> iterator = projectiles.values().iterator();
        while (iterator.hasNext()) {
            Projectile projectile = iterator.next();
            Position previousPosition = projectile.position();
            projectile.update(GameRules.FIXED_DELTA_SECONDS);

            Entity target = firstHitTarget(projectile, previousPosition);
            if (target != null) {
                int previousHealth = target.currentHealth();
                target.takeDamage(projectile.damage());
                if (target.currentHealth() < previousHealth) {
                    CombatEvent.Kind kind = target instanceof Player
                            ? CombatEvent.Kind.PLAYER_HIT
                            : CombatEvent.Kind.ENEMY_HIT;
                    combatEvents.add(new CombatEvent(kind, ""));
                }
                iterator.remove();
            } else if (projectile.isExpired() || isOutsideWorld(projectile)) {
                iterator.remove();
            }
        }
        enemies.values().removeIf(enemy -> !enemy.isAlive());

        enemySpawner.tick(GameRules.FIXED_DELTA_SECONDS, players.values(), enemies.values(), bounds)
                .ifPresent(this::spawn);

        return snapshot();
    }

    public synchronized boolean spawn(Enemy enemy) {
        Objects.requireNonNull(enemy);
        if (enemies.containsKey(enemy.id())) {
            return false;
        }
        enemies.put(enemy.id(), enemy);
        logger.log("Spawning enemy " + enemy.id());
        return true;
    }

    public synchronized boolean despawn(String enemyId) {
        Objects.requireNonNull(enemyId);
        return enemies.remove(enemyId) != null;
    }

    public synchronized void executeCommand(GameCommand command) {
        commandInvoker.execute(Objects.requireNonNull(command));
    }

    public synchronized void spawnEnemy(EnemyType type, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);
        String enemyId = "debug-" + nextDebugSpawnId++;
        executeCommand(new SpawnEnemyCommand(this, EnemyFactory.create(type, enemyId, position)));
    }

    public synchronized boolean undoLastCommand() {
        logger.log("Undoing command");
        return commandInvoker.undo();
    }

    public synchronized boolean redoLastCommand() {
        logger.log("Redoing command");
        return commandInvoker.redo();
    }

    public synchronized GameSnapshot snapshot() {
        return snapshotMapper.gameSnapshot(players.values(), enemies.values(), projectiles.values());
    }

    public synchronized List<CombatEvent> drainCombatEvents() {
        List<CombatEvent> events = List.copyOf(combatEvents);
        combatEvents.clear();
        return events;
    }

    private void addAttack(List<Projectile> attack) {
        if (attack.isEmpty()) {
            return;
        }
        attack.forEach(projectile -> projectiles.put(projectile.id(), projectile));
        // A spread or radial attack is one event, regardless of its projectile count.
        Projectile first = attack.get(0);
        CombatEvent.Kind kind = first.side() == ProjectileSide.PLAYER
                ? CombatEvent.Kind.PLAYER_SHOT
                : CombatEvent.Kind.ENEMY_SHOT;
        combatEvents.add(new CombatEvent(kind, first.spriteId()));
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
