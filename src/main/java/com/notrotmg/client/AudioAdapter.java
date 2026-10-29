package com.notrotmg.client;

import com.notrotmg.common.logging.GameLogger;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Object adapter from game sound identifiers to JavaFX audio clips. */
public final class AudioAdapter implements GameAudio, AutoCloseable {
    private static final double VOLUME = 0.4;

    private final Map<SoundEffect, AudioClip> clips = new EnumMap<>(SoundEffect.class);

    public AudioAdapter() {
        for (SoundEffect effect : SoundEffect.values()) {
            URL resource = AudioAdapter.class.getResource(effect.resourcePath());
            if (resource == null) {
                GameLogger.getInstance().log("Sound unavailable: " + effect.resourcePath());
                continue;
            }
            try {
                clips.put(effect, new AudioClip(resource.toExternalForm()));
            } catch (RuntimeException exception) {
                GameLogger.getInstance().log("Could not load sound " + effect + ": " + exception.getMessage());
            }
        }
    }

    @Override
    public synchronized void play(SoundEffect effect) {
        AudioClip clip = clips.get(Objects.requireNonNull(effect));
        if (clip == null) {
            return;
        }
        try {
            clip.play(VOLUME);
        } catch (RuntimeException exception) {
            clips.remove(effect);
            GameLogger.getInstance().log("Could not play sound " + effect + ": " + exception.getMessage());
        }
    }

    @Override
    public synchronized void close() {
        clips.values().forEach(AudioClip::stop);
        clips.clear();
    }
}
