package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

public final class WorldMapSetup {
    private WorldMapSetup() {}

    public static MapFeature createWorld() {
        MapFeature map = new BaseMap();

        map = new GraveyardDecorator(
                map,
                new Position(500, 400)
        );

        map = new GraveyardDecorator(
                map,
                new Position(1200, 800)
        );

        return map;
    }
}