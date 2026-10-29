package com.notrotmg.client;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Position;
import com.notrotmg.protocol.GameProtocol;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.function.Consumer;

public final class GameClientApplication extends Application implements GameClientListener {
    private final Canvas canvas = new Canvas(GameRules.VIEWPORT_WIDTH, GameRules.VIEWPORT_HEIGHT);
    private final Label statusLabel = new Label("Disconnected");
    private GameSessionFacade session;

    @Override
    public void start(Stage stage) {
        session = new GameSessionFacade(canvas, GameProtocol.LOCAL_SERVER_URI);

        Label instructions = new Label(
                "Move: WASD / arrow keys     Shoot: hold left mouse button"
                        + "     Spawn: Ctrl+E     Undo: Ctrl+Z"
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

        StackPane screen = new StackPane(root);
        Scene scene = new Scene(screen);
        scene.getStylesheets().add(Objects.requireNonNull(
                getClass().getResource("game.css"),
                "Missing client stylesheet: game.css"
        ).toExternalForm());

        StackPane classMenu = new StackPane();
        classMenu.getStyleClass().add("class-menu");
        classMenu.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        addClassChoices(classMenu, characterClass -> {
            screen.getChildren().remove(classMenu);
            statusLabel.setText("Connecting to " + GameProtocol.LOCAL_SERVER_URI + " ...");
            KeyboardMouseInput keyboard = new KeyboardMouseInput(scene, stage, canvas);
            GameControls controls = new GameControls(session, new DualSenseInput(), keyboard);
            scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                    session.undo();
                    event.consume();
                } else if (event.isControlDown() && event.getCode() == KeyCode.E) {
                    Position target = keyboard.aimTarget(session.playerScreenPosition());
                    session.spawnEnemy(target.x(), target.y());
                    event.consume();
                }
            });
            session.start(scene, stage, this, controls, characterClass);
        });
        screen.getChildren().add(classMenu);

        stage.setTitle("NotROTMG local client");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    private void addClassChoices(StackPane menu, Consumer<CharacterClass> onSelected) {
        HBox choices = new HBox(18);
        choices.setAlignment(Pos.CENTER);
        for (CharacterClass characterClass : CharacterClass.values()) {
            ImageView sprite = new ImageView(session.classSprite(characterClass));
            sprite.setFitWidth(64);
            sprite.setFitHeight(64);
            sprite.setPreserveRatio(true);
            sprite.setSmooth(false);

            Label name = new Label(characterClass.name());
            name.getStyleClass().add("class-name");
            VBox content = new VBox(10, sprite, name);
            content.setAlignment(Pos.CENTER);

            Button choice = new Button();
            choice.setGraphic(content);
            choice.setPrefSize(130, 130);
            choice.setMinSize(130, 130);
            choice.getStyleClass().add("class-choice");
            choice.setOnAction(event -> onSelected.accept(characterClass));
            choices.getChildren().add(choice);
        }
        menu.getChildren().add(choices);
    }

    @Override
    public void stop() {
        if (session != null) {
            session.close();
        }
    }

    @Override
    public void onConnected() {
        Platform.runLater(() -> statusLabel.setText("Connected"));
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
