package com.notrotmg.common.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class GameLogger {
    private static final GameLogger instance = new GameLogger();
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");

    private GameLogger() {
    }

    public static GameLogger getInstance() {
        return instance;
    }

    public synchronized void log(String message) {
        Date date = new Date();
        System.out.println(formatter.format(date) + " " + message);
    }
}
