package com.notrotmg.notrotmg;

/** The currently held movement direction. Each axis is normalized by the server. */
public record MoveCommand(int dx, int dy) {
}
