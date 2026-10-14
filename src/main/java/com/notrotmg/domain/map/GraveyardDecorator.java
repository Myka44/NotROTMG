package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

import java.util.ArrayList;
import java.util.List;

public final class GraveyardDecorator extends MapDecorator {
    private final Position origin;

    public GraveyardDecorator(MapFeature wrapped, Position origin) {
        super(wrapped);
        this.origin = origin;
    }

    @Override
    public List<MapObject> objects() {
        List<MapObject> result = super.objects();

        Building cemetery = new GravestoneRows(new Cemetery());
        result.addAll(cemetery.objectsAt(origin));

        return result;
    }
}