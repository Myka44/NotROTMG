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
    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        return switch (type) {
            case REGULAR -> decorateRegularEnemy(new RegularEnemy(id, position));
            case BOSS -> new BossEnemy(id, position);
        };
    }

    private static RegularEnemy decorateRegularEnemy(RegularEnemy enemy) {
        List<UnaryOperator<RegularEnemy>> decorators = new ArrayList<>(List.of(
                regularEnemy -> new RegularEnemyHealthDecorator(regularEnemy).decorate(),
                regularEnemy -> new RegularEnemySpreadShotDecorator(regularEnemy).decorate(),
                regularEnemy -> new RegularEnemySpeedDecorator(regularEnemy).decorate()
        ));
        Collections.shuffle(decorators, ThreadLocalRandom.current());

        for (int index = 0; index < 2; index++) {
            enemy = decorators.get(index).apply(enemy);
        }
        return enemy;
    }
}
