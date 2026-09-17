package com.notrotmg.notrotmg;

import java.util.List;

/** The authoritative state sent from the server to the client. */
public record GameState(
        int canvasWidth,
        int canvasHeight,
        List<PlayerState> players
) {
}
