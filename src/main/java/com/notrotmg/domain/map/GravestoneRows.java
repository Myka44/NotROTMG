package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

import java.util.ArrayList;
import java.util.List;

public final class GravestoneRows extends BuildingDecorator {
    private static final int ROWS = 5;
    private static final int COLUMNS = 4;
    private static final double SPACING_X = 32;
    private static final double SPACING_Y = 32;

    public GravestoneRows(Building wrapped) {
        super(wrapped);
    }

    @Override
    public List<MapObject> objectsAt(Position origin) {
        List<MapObject> result = super.objectsAt(origin);

        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                result.add(new MapObject(
                        "grave-" + row + "-" + column,
                        "gravestone",
                        new Position(
                                origin.x() + 16 + column * SPACING_X,
                                origin.y() + 16 + row * SPACING_Y
                        ),
                        16,
                        20,
                        true
                ));
            }
        }

        return result;
    }
}