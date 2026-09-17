package com.notrotmg.shared;

public class PlayerJoin implements Message {
    public final String playerId;
    public final String playerName;
    public final int color;

    public PlayerJoin(String playerId, String playerName, int color) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.color = color;
    }

    @Override
    public String getType() {
        return "player_join";
    }
}
