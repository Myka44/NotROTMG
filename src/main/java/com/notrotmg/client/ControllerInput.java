package com.notrotmg.client;

import com.notrotmg.domain.Position;
import com.notrotmg.domain.Vector2;

public interface ControllerInput extends AutoCloseable {
    void poll();

    Vector2 movement();

    Position aimTarget(Position playerScreenPosition);

    boolean isFiring();

    default boolean isConnected() {
        return true;
    }

    @Override
    void close();
}
