package com.notrotmg.domain.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class MapDecorator implements MapFeature {
    protected final MapFeature wrapped;

    protected MapDecorator(MapFeature wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public List<MapObject> objects() {
        return new ArrayList<>(wrapped.objects());
    }
}