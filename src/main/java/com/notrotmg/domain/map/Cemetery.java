package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

import java.util.List;

public final class Cemetery implements Building {
    @Override
    public List<MapObject> objectsAt(Position origin) {
        return List.of(new MapObject(
                "cemetery",
                "cemetery",
                origin,
                160,
                192,
                true
        ));
    }
}