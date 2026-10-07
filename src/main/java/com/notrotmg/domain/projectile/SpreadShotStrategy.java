package com.notrotmg.domain.projectile;

import java.util.ArrayList;
import java.util.List;

/** Fires several projectiles with an even angular gap centred on the aim direction. */
public final class SpreadShotStrategy implements AttackStrategy {
    private final int projectileCount;
    private final double angleStepDegrees;

    public SpreadShotStrategy(int projectileCount, double angleStepDegrees) {
        if (projectileCount < 2) {
            throw new IllegalArgumentException("A spread attack requires at least two projectiles");
        }
        if (angleStepDegrees <= 0) {
            throw new IllegalArgumentException("Spread angle step must be positive");
        }
        this.projectileCount = projectileCount;
        this.angleStepDegrees = angleStepDegrees;
    }

    @Override
    public List<ActiveProjectile> attack(AttackContext context, ProjectileDefinition definition) {
        List<ActiveProjectile> projectiles = new ArrayList<>(projectileCount);
        double centreIndex = (projectileCount - 1) / 2.0;

        for (int index = 0; index < projectileCount; index++) {
            double offsetRadians = Math.toRadians((index - centreIndex) * angleStepDegrees);
            projectiles.add(ActiveProjectile.launch(
                    context,
                    definition,
                    context.aimDirection().rotated(offsetRadians)
            ));
        }

        return List.copyOf(projectiles);
    }
}
