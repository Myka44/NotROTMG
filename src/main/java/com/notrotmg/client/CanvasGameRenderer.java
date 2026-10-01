package com.notrotmg.client;

import com.notrotmg.protocol.GameSnapshot;
import com.notrotmg.protocol.PlayerSnapshot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

public final class CanvasGameRenderer {
    private static final Color BACKGROUND = Color.web("#171923");
    private static final Color PLAYER_COLOR = Color.web("#55d6be");
    private final Canvas canvas;

    public CanvasGameRenderer(Canvas canvas) {
        this.canvas = Objects.requireNonNull(canvas);
    }

    public void render(GameSnapshot snapshot) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        graphics.setFill(BACKGROUND);
        graphics.fillRect(0, 0, snapshot.canvasWidth(), snapshot.canvasHeight());
        graphics.setFill(PLAYER_COLOR);
        for (PlayerSnapshot player : snapshot.players()) {
            graphics.fillRect(player.x(), player.y(), player.size(), player.size());
        }
    }
}
