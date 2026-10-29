package com.notrotmg.domain.enemy;

import com.notrotmg.domain.Position;

public interface EnemyPrototype {
    Enemy copy(String id, Position position);
}
