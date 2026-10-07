package com.notrotmg.domain.projectile;

import java.util.List;

/** Fires one projectile in the requested aim direction. */
public final class SingleShotStrategy implements AttackStrategy {
    @Override
    public List<ActiveProjectile> attack(AttackContext context, ProjectileDefinition definition) {
        return List.of(ActiveProjectile.launch(context, definition, context.aimDirection()));
    }
}
