package com.notrotmg.server;

import com.notrotmg.domain.Player;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.WorldBounds;
import com.notrotmg.domain.enemy.Enemy;
import com.notrotmg.domain.enemy.EnemyFactory;
import com.notrotmg.domain.enemy.EnemyType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

/** Chooses one bounded, player-aware enemy spawn every few seconds. */
final class EnemySpawner {
    private static final double INTERVAL_SECONDS = 5;
    private static final int MAX_ENEMIES = 20;
    private static final int MAX_BOSSES = 1;
    private static final int REGULAR_WEIGHT = 9;
    private static final int BOSS_WEIGHT = 1;
    private static final double MIN_PLAYER_DISTANCE = 300;
    private static final double MAX_PLAYER_DISTANCE = 600;
    private static final int POSITION_ATTEMPTS = 20;

    private double elapsedSeconds;
    private long nextSpawnId = 1;

    Optional<Enemy> tick(
            double deltaSeconds,
            Collection<Player> players,
            Collection<Enemy> enemies,
            WorldBounds bounds
    ) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        List<Player> livingPlayers = players.stream().filter(Player::isAlive).toList();
        if (livingPlayers.isEmpty()) {
            elapsedSeconds = 0;
            return Optional.empty();
        }

        elapsedSeconds += deltaSeconds;
        if (elapsedSeconds + 1e-9 < INTERVAL_SECONDS) {
            return Optional.empty();
        }
        elapsedSeconds = Math.max(0, elapsedSeconds - INTERVAL_SECONDS);
        if (enemies.size() >= MAX_ENEMIES) {
            return Optional.empty();
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        long bossCount = enemies.stream().filter(enemy -> enemy.type() == EnemyType.BOSS).count();
        int bossWeight = bossCount < MAX_BOSSES ? BOSS_WEIGHT : 0;
        EnemyType type = random.nextInt(REGULAR_WEIGHT + bossWeight) < REGULAR_WEIGHT
                ? EnemyType.REGULAR : EnemyType.BOSS;
        double enemySize = EnemyFactory.sizeOf(type);

        for (int attempt = 0; attempt < POSITION_ATTEMPTS; attempt++) {
            Player anchor = livingPlayers.get(random.nextInt(livingPlayers.size()));
            double radius = random.nextDouble(MIN_PLAYER_DISTANCE, MAX_PLAYER_DISTANCE);
            double angle = random.nextDouble(0, Math.PI * 2);
            double x = anchor.center().x() + Math.cos(angle) * radius - enemySize / 2;
            double y = anchor.center().y() + Math.sin(angle) * radius - enemySize / 2;
            if (x < 0 || y < 0 || x + enemySize > bounds.width()
                    || y + enemySize > bounds.height()) {
                continue;
            }

            Position center = new Position(x + enemySize / 2, y + enemySize / 2);
            boolean tooClose = livingPlayers.stream().anyMatch(player -> {
                double dx = center.x() - player.center().x();
                double dy = center.y() - player.center().y();
                return dx * dx + dy * dy < MIN_PLAYER_DISTANCE * MIN_PLAYER_DISTANCE;
            });
            if (!tooClose) {
                String id = "auto-" + nextSpawnId++;
                return Optional.of(EnemyFactory.create(type, id, new Position(x, y)));
            }
        }
        return Optional.empty();
    }
}
