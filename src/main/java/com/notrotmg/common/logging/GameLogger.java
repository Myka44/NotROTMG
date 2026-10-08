package com.notrotmg.common.logging;

public final class GameLogger {
    private static final GameLogger instance = new GameLogger();

    private GameLogger() {
    }

    public static GameLogger getInstance() {
        return instance;
    }

    public void log(String message) {
        System.out.println(message);
    }
}
