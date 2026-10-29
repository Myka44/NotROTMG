package com.notrotmg.domain.projectile;

import java.util.List;
import java.util.function.Supplier;

/** Launches one projectile whose direction turns continuously as it travels. */
public final class CurvedShotStrategy implements AttackStrategy {
    private static final double TURN_RATE_RADIANS_PER_SECOND = Math.toRadians(55);

    @Override
    public CurvedShotStrategy copy() {
        return new CurvedShotStrategy();
    }

    @Override
    public List<Projectile> attack(
            AttackContext context,
            ProjectileSpec spec,
            Supplier<ProjectileBuilder> builderFactory
    ) {
        Projectile projectile = AttackStrategy.launch(
                context, spec, builderFactory.get(), context.aimDirection()
        );
        projectile.setTurnRate(TURN_RATE_RADIANS_PER_SECOND);
        return List.of(projectile);
    }
}
