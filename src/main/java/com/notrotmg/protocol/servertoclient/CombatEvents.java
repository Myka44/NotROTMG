package com.notrotmg.protocol.servertoclient;

import java.util.List;

/** Transient combat events, published once rather than repeated in world snapshots. */
public record CombatEvents(List<CombatEvent> events) implements ServerMessage {
    public CombatEvents {
        events = List.copyOf(events);
    }
}
