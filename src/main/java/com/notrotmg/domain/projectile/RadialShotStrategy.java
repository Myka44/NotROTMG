package com.notrotmg.domain.projectile;

import java.util.ArrayList;
import java.util.List;

/** Fires projectiles evenly in every direction, starting from the aim direction. */
public final class RadialShotStrategy implements AttackStrategy {
    private final int projectileCount;

    public RadialShotStrategy(int projectileCount) {
        if (projectileCount < 2) {
            throw new IllegalArgumentException("A radial attack requires at least two projectiles");
        }
        this.projectileCount = projectileCount;
    }

    @Override
    public List<ActiveProjectile> attack(AttackContext context, ProjectileDefinition definition) {
        List<ActiveProjectile> projectiles = new ArrayList<>(projectileCount);
        double angleStepRadians = Math.PI * 2 / projectileCount;

        for (int index = 0; index < projectileCount; index++) {
            projectiles.add(ActiveProjectile.launch(
                    context,
                    definition,
                    context.aimDirection().rotated(index * angleStepRadians)
            ));
        }

        return List.copyOf(projectiles);
    }
}
