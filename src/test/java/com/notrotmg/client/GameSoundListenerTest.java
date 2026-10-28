package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.protocol.servertoclient.CombatEvent;
import com.notrotmg.protocol.servertoclient.CombatEvents;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.ServerMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameSoundListenerTest {
    @Test
    void playsDistinctWeaponEnemyAndHitSounds() {
        List<SoundEffect> played = new ArrayList<>();
        GameSoundListener listener = new GameSoundListener(played::add);

        listener.onMessage(new CombatEvents(List.of(
                new CombatEvent(CombatEvent.Kind.PLAYER_SHOT, "iron-sword-projectile"),
                new CombatEvent(CombatEvent.Kind.PLAYER_SHOT, "arrow-projectile"),
                new CombatEvent(CombatEvent.Kind.PLAYER_SHOT, "magic-bolt-projectile"),
                new CombatEvent(CombatEvent.Kind.ENEMY_SHOT, "guardian-projectile"),
                new CombatEvent(CombatEvent.Kind.ENEMY_HIT, ""),
                new CombatEvent(CombatEvent.Kind.PLAYER_HIT, "")
        )));

        assertEquals(List.of(
                SoundEffect.SWORD_SHOT,
                SoundEffect.BOW_SHOT,
                SoundEffect.MAGIC_SHOT,
                SoundEffect.ENEMY_SHOT,
                SoundEffect.HIT,
                SoundEffect.PLAYER_HIT
        ), played);
    }

    @Test
    void renderingOrResendingSnapshotsDoesNotPlaySounds() {
        List<SoundEffect> played = new ArrayList<>();
        GameSoundListener listener = new GameSoundListener(played::add);
        GameSnapshot snapshot = new GameSnapshot(640, 480, List.of(), List.of(), List.of(), "player-1");

        listener.onMessage(snapshot);
        listener.onMessage(snapshot);

        assertTrue(played.isEmpty());
    }

    @Test
    void decodesCombatEventsThroughTheExistingServerMessageProtocol() {
        JacksonJsonCodec codec = new JacksonJsonCodec();
        CombatEvents original = new CombatEvents(List.of(
                new CombatEvent(CombatEvent.Kind.PLAYER_SHOT, "arrow-projectile"),
                new CombatEvent(CombatEvent.Kind.PLAYER_HIT, "")
        ));

        ServerMessage decoded = codec.decode(codec.encode(original), ServerMessage.class);

        assertEquals(original, decoded);
        List<SoundEffect> played = new ArrayList<>();
        new GameSoundListener(played::add).onMessage(decoded);
        assertEquals(List.of(SoundEffect.BOW_SHOT, SoundEffect.PLAYER_HIT), played);
    }
}
