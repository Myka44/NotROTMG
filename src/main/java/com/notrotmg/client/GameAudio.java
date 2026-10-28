package com.notrotmg.client;

/** Game-facing audio interface, independent of the playback library. */
public interface GameAudio {
    void play(SoundEffect effect);
}
