package com.notrotmg.client;

import com.notrotmg.domain.GameRules;
import com.notrotmg.protocol.GameProtocol;
import com.notrotmg.protocol.servertoclient.ServerMessage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public final class GameClientApplication extends Application implements GameClientListener {
    private final Canvas canvas = new Canvas(GameRules.CANVAS_WIDTH, GameRules.CANVAS_HEIGHT);
    private final Label statusLabel = new Label("Disconnected");
    private GameSessionFacade session;

    @Override
    public void start(Stage stage) {
        session = new GameSessionFacade(canvas, GameProtocol.LOCAL_SERVER_URI);

        Label instructions = new Label(
                "Move: WASD / arrow keys     Shoot: hold left mouse button"
        );
        instructions.getStyleClass().add("instructions-label");

        StackPane gameFrame = new StackPane(canvas);
        gameFrame.setPadding(new Insets(3));
        gameFrame.getStyleClass().add("game-frame");

        BorderPane root = new BorderPane(gameFrame);
        root.getStyleClass().add("game-root");
        root.setTop(instructions);
        root.setRight(session.inventoryView());
        root.setBottom(statusLabel);
        BorderPane.setMargin(instructions, new Insets(8));
        BorderPane.setMargin(statusLabel, new Insets(8));
        BorderPane.setMargin(session.inventoryView(), new Insets(0, 0, 0, 8));
        statusLabel.getStyleClass().add("status-label");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("game.css"),
                "Missing client stylesheet: game.css"
        ).toExternalForm());

        session.addListener(this);
        new InputController(session).attach(scene, stage, canvas);

        stage.setTitle("NotROTMG local client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        statusLabel.setText("Connecting to " + GameProtocol.LOCAL_SERVER_URI + " ...");
        session.connect();
    }

    @Override
    public void stop() {
        if (session != null) {
            session.removeListener(this);
            session.close();
        }
    }

    @Override
    public void onConnected() {
        Platform.runLater(() -> statusLabel.setText("Connected"));
    }

    @Override
    public void onMessage(ServerMessage message) {
        Platform.runLater(() -> session.handleServerMessage(message));
    }

    @Override
    public void onDisconnected(int statusCode, String reason) {
        Platform.runLater(() -> statusLabel.setText("Disconnected (" + statusCode + ")"));
    }

    @Override
    public void onError(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }
}
