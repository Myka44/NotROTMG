package com.notrotmg.client;

import javafx.application.Application;

public final class ClientLauncher {
    private ClientLauncher() {
    }

    public static void main(String[] args) {
        Application.launch(GameClientApplication.class, args);
    }
}
