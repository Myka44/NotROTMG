package com.notrotmg.shared;

import java.util.List;
import java.util.Map;

public class StateSnapshot implements Message {
    public final Map<String, PlayerState> players;
    public final List<EnemyState> enemies;
    public final List<MapElementState> mapElements;

    public StateSnapshot(Map<String, PlayerState> players, List<EnemyState> enemies, List<MapElementState> mapElements) {
        this.players = players;
        this.enemies = enemies;
        this.mapElements = mapElements;
    }

    @Override
    public String getType() {
        return "state_snapshot";
    }

    public static StateSnapshot fromJson(String json) {
        return new com.google.gson.Gson().fromJson(json, StateSnapshot.class);
    }

    public record PlayerState(String id, String name, int x, int y, int color, int hp) {}
    public record EnemyState(int id, int x, int y, int type, int hp) {}
    public record MapElementState(int id, int x, int y, int type) {}
}
