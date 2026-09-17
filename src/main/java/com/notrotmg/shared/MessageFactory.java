package com.notrotmg.shared;

public class MessageFactory {
    public static Message createPlayerJoin(String playerId, String playerName, int color) {
        return new PlayerJoin(playerId, playerName, color);
    }

    public static Message createPlayerLeave(String playerId) {
        return new PlayerLeave(playerId);
    }

    public static Message createStateSnapshot(java.util.Map<String, StateSnapshot.PlayerState> players,
                                              java.util.List<StateSnapshot.EnemyState> enemies,
                                              java.util.List<StateSnapshot.MapElementState> mapElements) {
        return new StateSnapshot(players, enemies, mapElements);
    }
}
