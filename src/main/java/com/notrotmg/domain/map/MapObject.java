package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

public record MapObject(
        String id,
        String type,
        Position position,
        double width,
        double height,
        boolean solid
) {}