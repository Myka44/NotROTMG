package com.notrotmg.client;

import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.PlayerSnapshot;
import com.notrotmg.protocol.servertoclient.ProjectileSnapshot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Objects;

/** Draws a deliberately small, pixel-art-inspired top-down game view. */
public final class CanvasGameRenderer {
    private static final int TILE_SIZE = 32;
    private static final Color TILE_A = Color.web("#334b3b");
    private static final Color TILE_B = Color.web("#304536");
    private static final Color GRID = Color.web("#26382d");
    private static final Color STONE = Color.web("#71806f");
    private final Canvas canvas;
    private final SpriteCatalog spriteCatalog;

    public CanvasGameRenderer(Canvas canvas, SpriteCatalog spriteCatalog) {
        this.canvas = Objects.requireNonNull(canvas);
        this.spriteCatalog = Objects.requireNonNull(spriteCatalog);
    }

    public void render(GameSnapshot snapshot) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        drawGround(graphics, snapshot.canvasWidth(), snapshot.canvasHeight());
        for (ProjectileSnapshot projectile : snapshot.projectiles()) {
            drawProjectile(graphics, projectile);
        }
        for (PlayerSnapshot player : snapshot.players()) {
            drawPlayer(graphics, player, snapshot.canvasHeight());
        }
    }

    private void drawProjectile(GraphicsContext graphics, ProjectileSnapshot projectile) {
        double angleDegrees = Math.toDegrees(Math.atan2(
                projectile.trajectoryY(),
                projectile.trajectoryX()
        ));

        graphics.save();
        graphics.translate(projectile.x(), projectile.y());
        graphics.rotate(angleDegrees);
        graphics.setFill(projectileColor(projectile.spriteId()));
        graphics.fillRect(
                -projectile.length() / 2,
                -2,
                projectile.length(),
                4
        );
        graphics.restore();
    }

    private Color projectileColor(String spriteId) {
        return switch (spriteId) {
            case "iron-sword-projectile" -> Color.web("#d8e1e8");
            case "arrow-projectile" -> Color.web("#e6c36a");
            case "magic-bolt-projectile" -> Color.web("#a978e8");
            case "slime-projectile" -> Color.web("#68c96b");
            case "guardian-projectile" -> Color.web("#df65c8");
            default -> Color.web("#f5d76e");
        };
    }

    private void drawGround(GraphicsContext graphics, int width, int height) {
        for (int y = 0; y < height; y += TILE_SIZE) {
            for (int x = 0; x < width; x += TILE_SIZE) {
                int column = x / TILE_SIZE;
                int row = y / TILE_SIZE;
                graphics.setFill((column + row) % 2 == 0 ? TILE_A : TILE_B);
                graphics.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                graphics.setStroke(GRID);
                graphics.strokeRect(x + 0.5, y + 0.5, TILE_SIZE - 1, TILE_SIZE - 1);

                if ((column * 13 + row * 7) % 17 == 0) {
                    graphics.setFill(STONE);
                    graphics.fillRect(x + 7, y + 20, 5, 3);
                    graphics.fillRect(x + 10, y + 18, 4, 4);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphics, PlayerSnapshot player, int canvasHeight) {
        double scale = player.size() / 16.0;
        double x = player.x();
        double y = player.y();

        graphics.setFill(Color.color(0, 0, 0, 0.28));
        graphics.fillOval(x + 3 * scale, y + 12 * scale, 11 * scale, 4 * scale);
        graphics.setImageSmoothing(false);
        graphics.drawImage(
                spriteCatalog.playerSprite(player.characterClass()),
                x,
                y,
                player.size(),
                player.size()
        );

        drawHealthBar(graphics, player, canvasHeight);
    }

    private void drawHealthBar(GraphicsContext graphics, PlayerSnapshot player, int canvasHeight) {
        double width = player.size();
        double y = player.y() + player.size() + 3;
        if (y + 4 > canvasHeight) {
            y = player.y() - 6;
        }
        double healthRatio = player.maxHealth() == 0
                ? 0
                : Math.max(0, Math.min(1, (double) player.currentHealth() / player.maxHealth()));

        graphics.setFill(Color.web("#181818"));
        graphics.fillRect(player.x(), y, width, 4);
        graphics.setFill(Color.web("#bf4545"));
        graphics.fillRect(player.x() + 1, y + 1, (width - 2) * healthRatio, 2);
    }
}
