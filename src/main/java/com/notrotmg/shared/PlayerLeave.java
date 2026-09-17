package com.notrotmg.shared;

public class PlayerLeave implements Message {
    public final String playerId;

    public PlayerLeave(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public String getType() {
        return "player_leave";
    }
}
