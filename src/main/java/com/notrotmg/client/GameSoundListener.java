package com.notrotmg.client;

import com.notrotmg.protocol.servertoclient.CombatEvent;
import com.notrotmg.protocol.servertoclient.CombatEvents;
import com.notrotmg.protocol.servertoclient.ServerMessage;

import java.util.Objects;

/** Observes confirmed server events and translates them into game sound requests. */
public final class GameSoundListener implements GameClientListener {
    private final GameAudio audio;

    public GameSoundListener(GameAudio audio) {
        this.audio = Objects.requireNonNull(audio);
    }

    @Override
    public void onMessage(ServerMessage message) {
        if (!(message instanceof CombatEvents combatEvents)) {
            return;
        }
        for (CombatEvent event : combatEvents.events()) {
            SoundEffect effect = switch (event.kind()) {
                case PLAYER_SHOT -> playerShotSound(event.spriteId());
                case ENEMY_SHOT -> SoundEffect.ENEMY_SHOT;
                case PLAYER_HIT -> SoundEffect.PLAYER_HIT;
                case ENEMY_HIT -> SoundEffect.HIT;
            };
            if (effect != null) {
                audio.play(effect);
            }
        }
    }

    private SoundEffect playerShotSound(String spriteId) {
        return switch (spriteId) {
            case "iron-sword-projectile" -> SoundEffect.SWORD_SHOT;
            case "arrow-projectile" -> SoundEffect.BOW_SHOT;
            case "magic-bolt-projectile" -> SoundEffect.MAGIC_SHOT;
            default -> null;
        };
    }
}
