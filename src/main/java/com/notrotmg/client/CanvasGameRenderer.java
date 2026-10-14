package com.notrotmg.client;

import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.EnemySnapshot;
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
        for (EnemySnapshot enemy : snapshot.enemies()) {
            drawEnemy(graphics, enemy, snapshot.canvasHeight());
        }
        for (PlayerSnapshot player : snapshot.players()) {
            drawPlayer(graphics, player, snapshot.canvasHeight());
        }
    }

    private void drawEnemy(GraphicsContext graphics, EnemySnapshot enemy, int canvasHeight) {
        double x = enemy.x();
        double y = enemy.y();
        double size = enemy.size();
        Color body = switch (enemy.type()) {
            case REGULAR -> Color.web("#65b66c");
            case BOSS -> Color.web("#8750a7");
        };

        graphics.setFill(Color.color(0, 0, 0, 0.28));
        graphics.fillOval(x + size * 0.12, y + size * 0.78, size * 0.8, size * 0.18);
        graphics.setFill(Color.web("#25232c"));
        graphics.fillRect(x + 1, y + 1, size - 2, size - 2);
        graphics.setFill(body);
        graphics.fillRect(x + 3, y + 3, size - 6, size - 6);
        graphics.setFill(Color.web("#f6e9d1"));
        graphics.fillRect(x + size * 0.28, y + size * 0.36, Math.max(2, size * 0.12), Math.max(2, size * 0.12));
        graphics.fillRect(x + size * 0.60, y + size * 0.36, Math.max(2, size * 0.12), Math.max(2, size * 0.12));
        drawHealthBar(graphics, x, y, size, enemy.currentHealth(), enemy.maxHealth(), canvasHeight);
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

        drawHealthBar(
                graphics,
                player.x(),
                player.y(),
                player.size(),
                player.currentHealth(),
                player.maxHealth(),
                canvasHeight
        );
    }

    private void drawHealthBar(
            GraphicsContext graphics,
            double x,
            double y,
            double size,
            int currentHealth,
            int maxHealth,
            int canvasHeight
    ) {
        double barY = y + size + 3;
        if (barY + 4 > canvasHeight) {
            barY = y - 6;
        }
        double healthRatio = maxHealth == 0
                ? 0
                : Math.max(0, Math.min(1, (double) currentHealth / maxHealth));

        graphics.setFill(Color.web("#181818"));
        graphics.fillRect(x, barY, size, 4);
        graphics.setFill(Color.web("#bf4545"));
        graphics.fillRect(x + 1, barY + 1, (size - 2) * healthRatio, 2);
    }
}
