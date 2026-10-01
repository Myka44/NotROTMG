package com.notrotmg.client;

import com.notrotmg.common.json.JacksonJsonCodec;
import com.notrotmg.domain.GameRules;
import com.notrotmg.protocol.GameProtocol;
import com.notrotmg.protocol.GameSnapshot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public final class GameClientApplication extends Application {
    private final Canvas canvas = new Canvas(GameRules.CANVAS_WIDTH, GameRules.CANVAS_HEIGHT);
    private final Label statusLabel = new Label("Disconnected");
    private GameClient gameClient;

    @Override
    public void start(Stage stage) {
        CanvasGameRenderer renderer = new CanvasGameRenderer(canvas);
        renderer.render(new GameSnapshot(GameRules.CANVAS_WIDTH, GameRules.CANVAS_HEIGHT, List.of()));

        Label instructions = new Label("Move with WASD or the arrow keys");
        BorderPane root = new BorderPane(canvas);
        root.setTop(instructions);
        root.setBottom(statusLabel);
        BorderPane.setMargin(instructions, new Insets(8));
        BorderPane.setMargin(statusLabel, new Insets(8));

        Scene scene = new Scene(root);
        gameClient = new GameClient(
                GameProtocol.LOCAL_SERVER_URI,
                new JacksonJsonCodec(),
                snapshot -> Platform.runLater(() -> renderer.render(snapshot)),
                status -> Platform.runLater(() -> statusLabel.setText(status))
        );
        new InputController(gameClient).attach(scene, stage);

        stage.setTitle("NotROTMG local client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        statusLabel.setText("Connecting to " + GameProtocol.LOCAL_SERVER_URI + " ...");
        gameClient.connect();
    }

    @Override
    public void stop() {
        if (gameClient != null) {
            gameClient.close();
        }
    }

}
