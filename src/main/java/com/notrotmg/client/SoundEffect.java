package com.notrotmg.client;

public enum SoundEffect {
    SWORD_SHOT("sword-shot.wav"),
    BOW_SHOT("bow-shot.wav"),
    MAGIC_SHOT("magic-shot.wav"),
    ENEMY_SHOT("enemy-shot.wav"),
    ENEMY_HIT("hit.wav"),
    PLAYER_HIT("player-hit.mp3");

    private final String fileName;

    SoundEffect(String fileName) {
        this.fileName = fileName;
    }

    public String resourcePath() {
        return "/sounds/" + fileName;
    }
}
