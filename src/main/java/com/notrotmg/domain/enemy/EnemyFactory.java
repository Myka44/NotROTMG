package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.enemy.decorators.RegularEnemyHealthDecorator;
import com.notrotmg.domain.enemy.decorators.RegularEnemySpreadShotDecorator;
import com.notrotmg.domain.enemy.decorators.RegularEnemySpeedDecorator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

/** Copies configured enemy templates and randomly decorates regular enemies. */
public final class EnemyFactory {
    private static final Position TEMPLATE_POSITION = new Position(0, 0);
    private static final RegularEnemy REGULAR_TEMPLATE =
            new RegularEnemy("regular-template", TEMPLATE_POSITION);
    private static final BossEnemy BOSS_TEMPLATE =
            new BossEnemy("boss-template", TEMPLATE_POSITION);
    private static final Map<EnemyType, EnemyPrototype> PROTOTYPES = Map.of(
            EnemyType.REGULAR, REGULAR_TEMPLATE,
            EnemyType.BOSS, BOSS_TEMPLATE
    );

    private EnemyFactory() {
    }

    public static Enemy create(EnemyType type, String id, Position position) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(position);

        Enemy copy = PROTOTYPES.get(type).copy(id, position);
        return type == EnemyType.REGULAR
                ? decorateRegularEnemy((RegularEnemy) copy)
                : copy;
    }

    public static double sizeOf(EnemyType type) {
        return switch (Objects.requireNonNull(type)) {
            case REGULAR -> REGULAR_TEMPLATE.size();
            case BOSS -> BOSS_TEMPLATE.size();
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
