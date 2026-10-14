package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class BuildingDecorator implements Building {
    protected final Building wrapped;

    protected BuildingDecorator(Building wrapped) {
        this.wrapped = Objects.requireNonNull(wrapped);
    }

    @Override
    public List<MapObject> objectsAt(Position origin) {
        return new ArrayList<>(wrapped.objectsAt(origin));
    }
}