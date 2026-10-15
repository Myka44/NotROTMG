package com.notrotmg.server;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Invoker that executes {@link GameCommand}s and keeps an undo/redo history.
 *
 * <p>The history is the seed of the eventual admin/replay tooling: any action
 * routed through {@link #execute(GameCommand)} can later be reversed.</p>
 */
public final class CommandInvoker {
    private final Deque<GameCommand> undoHistory = new ArrayDeque<>();
    private final Deque<GameCommand> redoHistory = new ArrayDeque<>();

    public void execute(GameCommand command) {
        Objects.requireNonNull(command).execute();
        undoHistory.push(command);
        redoHistory.clear();
    }

    /** Undoes the most recent command. Returns whether anything was undone. */
    public boolean undo() {
        if (undoHistory.isEmpty()) {
            return false;
        }
        GameCommand command = undoHistory.pop();
        command.undo();
        redoHistory.push(command);
        return true;
    }

    /** Re-applies the most recently undone command. */
    public boolean redo() {
        if (redoHistory.isEmpty()) {
            return false;
        }
        GameCommand command = redoHistory.pop();
        command.execute();
        undoHistory.push(command);
        return true;
    }

    public int historySize() {
        return undoHistory.size();
    }
}
