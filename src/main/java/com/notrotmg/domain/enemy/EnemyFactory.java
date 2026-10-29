
package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.enemy.decorators.RegularEnemyHealthDecorator;
import com.notrotmg.domain.enemy.decorators.RegularEnemySpreadShotDecorator;
import com.notrotmg.domain.enemy.decorators.RegularEnemySpeedDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

/** Creates enemies and randomly applies two regular-enemy decorators. */
public final class EnemyFactory {
    private static final RegularEnemy REGULAR_PROTOTYPE =
            new RegularEnemy("regular-prototype", new Position(0, 0));
    private static final BossEnemy BOSS_PROTOTYPE =
            new BossEnemy("boss-prototype", new Position(0, 0));

    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        return switch (type) {
            case REGULAR -> decorateRegularEnemy(REGULAR_PROTOTYPE.copy(id, position));
            case BOSS -> BOSS_PROTOTYPE.copy(id, position);
        };
    }

    public static double sizeOf(EnemyType type) {
        return switch (Objects.requireNonNull(type)) {
            case REGULAR -> REGULAR_PROTOTYPE.size();
            case BOSS -> BOSS_PROTOTYPE.size();
        };
    }

    private static Enemy decorateRegularEnemy(RegularEnemy enemy) {
        List<UnaryOperator<Enemy>> decorators = new ArrayList<>(List.of(
            RegularEnemyHealthDecorator::new,
            RegularEnemySpreadShotDecorator::new,
            RegularEnemySpeedDecorator::new
        ));

        Collections.shuffle(decorators, ThreadLocalRandom.current());

        Enemy decoratedEnemy = enemy;
        for (int index = 0; index < 2; index++) {
            decoratedEnemy = decorators.get(index).apply(decoratedEnemy);
        }

        return decoratedEnemy;
    }
}
