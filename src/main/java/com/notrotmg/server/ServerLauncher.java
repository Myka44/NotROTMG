package com.notrotmg.server;

public final class ServerLauncher {
    private ServerLauncher() {
    }

    public static void main(String[] args) {
        GameServer server = new GameServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close, "server-shutdown"));
        server.start();
    }
}
