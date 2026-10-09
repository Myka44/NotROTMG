package com.notrotmg.client;

import com.notrotmg.protocol.servertoclient.ServerMessage;

public interface GameClientListener {
    default void onConnected() {
    }

    default void onMessage(ServerMessage message) {
    }

    default void onDisconnected(int statusCode, String reason) {
    }

    default void onError(String message) {
    }
}
