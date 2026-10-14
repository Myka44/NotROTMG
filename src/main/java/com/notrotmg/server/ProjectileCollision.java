package com.notrotmg.server;

import com.notrotmg.domain.Entity;
import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;
import com.notrotmg.domain.projectile.Projectile;

/** Sweeps the visible projectile segment against an entity's square hitbox. */
final class ProjectileCollision {
    private static final double HALF_PROJECTILE_THICKNESS = 2;

    private ProjectileCollision() {
    }

    /** Returns the first hit along this tick's movement, or positive infinity when there is no hit. */
    static double hitFraction(Projectile projectile, Position previousProjectilePosition, Entity target) {
        Vector2 direction = projectile.trajectory();
        double halfLength = projectile.length() / 2;
        double startProjectileX = previousProjectilePosition.x() - direction.x() * halfLength;
        double startProjectileY = previousProjectilePosition.y() - direction.y() * halfLength;
        double endProjectileX = projectile.position().x() + direction.x() * halfLength;
        double endProjectileY = projectile.position().y() + direction.y() * halfLength;
        double dx = endProjectileX - startProjectileX;
        double dy = endProjectileY - startProjectileY;

        double targetLeft = target.position().x() - HALF_PROJECTILE_THICKNESS;
        double targetRight = target.position().x() + target.size() + HALF_PROJECTILE_THICKNESS;
        double targetTop = target.position().y() - HALF_PROJECTILE_THICKNESS;
        double targetBottom = target.position().y() + target.size() + HALF_PROJECTILE_THICKNESS;

        double entry = 0;
        double exit = 1;
        if (dx == 0) {
            if (startProjectileX < targetLeft || startProjectileX > targetRight) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double first = (targetLeft - startProjectileX) / dx;
            double last = (targetRight - startProjectileX) / dx;
            entry = Math.max(entry, Math.min(first, last));
            exit = Math.min(exit, Math.max(first, last));
        }
        if (dy == 0) {
            if (startProjectileY < targetTop || startProjectileY > targetBottom) {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            double first = (targetTop - startProjectileY) / dy;
            double last = (targetBottom - startProjectileY) / dy;
            entry = Math.max(entry, Math.min(first, last));
            exit = Math.min(exit, Math.max(first, last));
        }
        return entry <= exit ? entry : Double.POSITIVE_INFINITY;
    }
}
