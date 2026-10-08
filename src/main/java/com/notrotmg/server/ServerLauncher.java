package com.notrotmg.server;

import com.notrotmg.common.logging.GameLogger;

public final class ServerLauncher {
    private ServerLauncher() {
    }

    public static void main(String[] args) {
        GameLogger.getInstance().log("Starting game server...");
        GameServer server = new GameServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "server-shutdown"));
        server.start();
    }
}
