package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Vector2;

import java.util.List;
import java.util.function.Supplier;

/** Strategy for arranging the projectiles created by one attack. */
public interface AttackStrategy {
    List<Projectile> attack(
            AttackContext context,
            ProjectileSpec spec,
            Supplier<ProjectileBuilder> builderFactory
    );

    static Projectile launch(
            AttackContext context,
            ProjectileSpec spec,
            ProjectileBuilder builder,
            Vector2 trajectory
    ) {
        builder.setOwner(context.attackerId());
        builder.setMovement(context.origin(), trajectory);
        builder.setStats(spec);
        return builder.build();
    }
}
