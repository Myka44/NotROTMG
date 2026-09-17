package com.notrotmg.shared;

import com.google.gson.Gson;

public record PlayerInput(String playerId, int dx, int dy) implements Message {
    private static final Gson GSON = new Gson();

    @Override
    public String getType() {
        return "input";
    }
}
