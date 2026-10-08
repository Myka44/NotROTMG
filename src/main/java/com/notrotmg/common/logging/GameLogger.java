package com.notrotmg.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/** Application-wide logger implemented with the Singleton pattern. */
public final class GameLogger {
    private static final GameLogger INSTANCE = new GameLogger();

    private final Logger logger = LoggerFactory.getLogger(GameLogger.class);

    private GameLogger() {
    }

    public static GameLogger getInstance() {
        return INSTANCE;
    }

    public void log(String message) {
        logger.info(Objects.requireNonNull(message));
    }
}
