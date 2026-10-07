package com.notrotmg.protocol.clienttoserver;

/** Current held movement direction for one player. */
public record PlayerInput(int horizontal, int vertical) implements ClientCommand {
    public PlayerInput {
        horizontal = Integer.compare(horizontal, 0);
        vertical = Integer.compare(vertical, 0);
    }
}
