package com.notrotmg.domain;

public final class GameRules {
    public static final int CANVAS_WIDTH = 640;
    public static final int CANVAS_HEIGHT = 480;
    public static final int PLAYER_SIZE = 32;
    public static final int TICKS_PER_SECOND = 30;
    public static final double FIXED_DELTA_SECONDS = 1.0 / TICKS_PER_SECOND;

    private GameRules() {
    }
}
