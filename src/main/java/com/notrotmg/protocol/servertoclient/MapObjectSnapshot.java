package com.notrotmg.protocol.servertoclient;

/** Network representation of a static map object. */
public record MapObjectSnapshot(
        String id,
        String type,
        double x,
        double y,
        double width,
        double height,
        boolean solid
) {}