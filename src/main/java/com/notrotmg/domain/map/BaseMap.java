package com.notrotmg.domain.map;

import java.util.List;

public final class BaseMap implements MapFeature {
    @Override
    public List<MapObject> objects() {
        return List.of();
    }
}