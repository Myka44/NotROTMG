package com.notrotmg.protocol;

import java.util.List;

/** Complete authoritative world snapshot sent to every client. */
public record GameSnapshot(
        int canvasWidth,
        int canvasHeight,
        List<PlayerSnapshot> players
) {
    public GameSnapshot {
        players = List.copyOf(players);
    }
}
