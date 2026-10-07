package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;

/** Prototype contract for spawning a new enemy from an existing configured enemy. */
public interface EnemyPrototype {
    Enemy copy(String id, Position position);
}
