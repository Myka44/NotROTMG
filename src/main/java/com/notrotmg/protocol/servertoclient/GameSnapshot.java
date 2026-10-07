package com.notrotmg.protocol.servertoclient;

import java.util.List;

/** Complete authoritative world snapshot sent to every client. */
public record GameSnapshot(
        int canvasWidth,
        int canvasHeight,
        List<PlayerSnapshot> players
) implements Snapshot {
    public GameSnapshot {
        players = List.copyOf(players);
    }
}
