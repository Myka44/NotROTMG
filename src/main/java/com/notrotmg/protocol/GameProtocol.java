package com.notrotmg.protocol;

import java.net.URI;

public final class GameProtocol {
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 7070;
    public static final String WEBSOCKET_PATH = "/game";
    public static final int MAX_PLAYERS = 4;
    public static final URI LOCAL_SERVER_URI = URI.create(
            "ws://" + HOST + ":" + PORT + WEBSOCKET_PATH
    );

    private GameProtocol() {
    }
}
