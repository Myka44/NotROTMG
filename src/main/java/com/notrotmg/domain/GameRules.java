package com.notrotmg.domain;

public final class GameRules {
    public static final int VIEWPORT_WIDTH = 640;
    public static final int VIEWPORT_HEIGHT = 480;
    public static final int WORLD_WIDTH = 1920;
    public static final int WORLD_HEIGHT = 1440;
    public static final int PLAYER_SIZE = 32;
    public static final int TICKS_PER_SECOND = 30;
    public static final double FIXED_DELTA_SECONDS = 1.0 / TICKS_PER_SECOND;

    private GameRules() {
    }
}
