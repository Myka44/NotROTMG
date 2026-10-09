package com.notrotmg.domain.projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
    public List<Projectile> attack(
            AttackContext context,
            ProjectileSpec spec,
            Supplier<ProjectileBuilder> builderFactory
    ) {
        List<Projectile> projectiles = new ArrayList<>(projectileCount);
        double angleStepRadians = Math.PI * 2 / projectileCount;

        for (int index = 0; index < projectileCount; index++) {
            projectiles.add(AttackStrategy.launch(
                    context,
                    spec,
                    builderFactory.get(),
                    context.aimDirection().rotated(index * angleStepRadians)
            ));
        }

        return List.copyOf(projectiles);
    }
}
