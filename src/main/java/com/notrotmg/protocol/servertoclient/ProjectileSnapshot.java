package com.notrotmg.protocol.servertoclient;

/** Renderable state for one server-owned projectile. */
public record ProjectileSnapshot(
        String id,
        String spriteId,
        double x,
        double y,
        double trajectoryX,
        double trajectoryY,
        double length
) {
}
