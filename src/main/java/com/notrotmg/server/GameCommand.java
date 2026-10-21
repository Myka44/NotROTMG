package com.notrotmg.server;

public interface GameCommand {
    /** Performs the action. */
    void execute();

    /** Reverses a previously executed action. */
    void undo();
}
