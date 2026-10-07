package com.notrotmg.domain.projectile;

import java.util.List;

/** Strategy for arranging the projectiles created by one attack. */
public interface AttackStrategy {
    List<ActiveProjectile> attack(AttackContext context, ProjectileDefinition definition);
}
