package com.notrotmg.server;

import java.util.Objects;

/**
 * Command pattern contract: a game action encapsulated as an object that can be
 * executed and undone.
 *
 * <p>Use {@link #of(Runnable, Runnable)} to build a command from a do/undo pair
 * instead of declaring a class per action.</p>
 */
public interface GameCommand {
    /** Performs the action. */
    void execute();

    /** Reverses a previously executed action. */
    void undo();

    /** Adapts a do/undo pair to a {@link GameCommand}. */
    static GameCommand of(Runnable execute, Runnable undo) {
        Objects.requireNonNull(execute);
        Objects.requireNonNull(undo);
        return new GameCommand() {
            @Override
            public void execute() {
                execute.run();
            }

            @Override
            public void undo() {
                undo.run();
            }
        };
    }
}
