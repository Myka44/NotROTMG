
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
    private static final List<Enemy> PROTOTYPES = List.of(
        new RegularEnemy("regular-prototype", new Position(0, 0)),
        new BossEnemy("boss-prototype", new Position(0, 0))
    );

    private static int enemyNum = 1;

    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        return switch (type) {
            case REGULAR -> {
                enemyNum++;
                RegularEnemy enemy =
                    ((RegularEnemy) PROTOTYPES.get(0)).copy();

                enemy.setId(id + enemyNum);
                enemy.setPosition(position);

                yield decorateRegularEnemy(enemy);
            }
            case BOSS -> {
                BossEnemy enemy =
                    ((BossEnemy) PROTOTYPES.get(1)).copy();

                enemy.setId(id);
                enemy.setPosition(position);

                yield enemy;
            }
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
