package com.notrotmg.domain.projectile;

import java.util.List;
import java.util.function.Supplier;

/** Fires one projectile in the requested aim direction. */
public final class SingleShotStrategy implements AttackStrategy {
    @Override
    public List<Projectile> attack(
            AttackContext context,
            ProjectileSpec spec,
            Supplier<ProjectileBuilder> builderFactory
    ) {
        return List.of(AttackStrategy.launch(context, spec, builderFactory.get(), context.aimDirection()));
    }
}
