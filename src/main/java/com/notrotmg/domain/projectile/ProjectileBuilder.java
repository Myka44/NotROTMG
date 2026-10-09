package com.notrotmg.domain.projectile;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

public interface ProjectileBuilder {
    void setMovement(Position position, Vector2 trajectory);

    void setOwner(String ownerId);

    void setStats(ProjectileSpec spec);

    Projectile build();
}
