package com.notrotmg.domain.map;

import com.notrotmg.domain.Position;

import java.util.List;

public interface Building {
    List<MapObject> objectsAt(Position origin);
}