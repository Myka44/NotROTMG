package com.notrotmg.domain.projectile;

import java.util.List;
import java.util.function.Supplier;

/** Fires four projectiles in perpendicular directions to form a plus-shaped cross. */
public final class CrossShotStrategy implements AttackStrategy {
    @Override
    public CrossShotStrategy copy() {
        return new CrossShotStrategy();
    }

    @Override
    public List<Projectile> attack(
            AttackContext context,
            ProjectileSpec spec,
            Supplier<ProjectileBuilder> builderFactory
    ) {
        return List.of(
            AttackStrategy.launch(context, spec, builderFactory.get(), context.aimDirection()),
            AttackStrategy.launch(context, spec, builderFactory.get(), context.aimDirection().rotated(Math.PI / 2)),
            AttackStrategy.launch(context, spec, builderFactory.get(), context.aimDirection().rotated(Math.PI)),
            AttackStrategy.launch(context, spec, builderFactory.get(), context.aimDirection().rotated(3 * Math.PI / 2))
        );
    }
}
