package com.notrotmg.server;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/** Fixed-rate loop for advancing and publishing the authoritative game state. */
public final class GameLoop implements AutoCloseable {
    private final int ticksPerSecond;
    private final Consumer<Throwable> errorHandler;
    private final AtomicBoolean started = new AtomicBoolean();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "game-loop");
        thread.setDaemon(true);
        return thread;
    });

    public GameLoop(int ticksPerSecond, Consumer<Throwable> errorHandler) {
        if (ticksPerSecond <= 0) {
            throw new IllegalArgumentException("Tick rate must be positive");
        }
        this.ticksPerSecond = ticksPerSecond;
        this.errorHandler = Objects.requireNonNull(errorHandler);
    }

    public void start(Runnable tick) {
        Objects.requireNonNull(tick);
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("Game loop has already started");
        }

        long period = 1_000_000_000L / ticksPerSecond;
        executor.scheduleAtFixedRate(
                () -> runSafely(tick),
                0,
                period,
                TimeUnit.NANOSECONDS
        );
    }

    private void runSafely(Runnable tick) {
        try {
            tick.run();
        } catch (Throwable error) {
            errorHandler.accept(error);
        }
    }

    @Override
    public void close() {
        executor.shutdownNow();
    }
}
