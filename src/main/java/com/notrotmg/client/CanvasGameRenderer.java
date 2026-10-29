package com.notrotmg.client;

import com.notrotmg.domain.Position;
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
    private double cameraX;
    private double cameraY;
    private Position playerScreenPosition;
    private Position aimTarget;

    public CanvasGameRenderer(Canvas canvas, SpriteCatalog spriteCatalog) {
        this.canvas = Objects.requireNonNull(canvas);
        this.spriteCatalog = Objects.requireNonNull(spriteCatalog);
        this.playerScreenPosition = new Position(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    public void render(GameSnapshot snapshot) {
        GraphicsContext graphics = canvas.getGraphicsContext2D();
        updateCamera(snapshot);
        graphics.save();
        graphics.beginPath();
        graphics.rect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.clip();
        graphics.translate(-cameraX, -cameraY);
        drawGround(graphics, snapshot.worldWidth(), snapshot.worldHeight());
        for (ProjectileSnapshot projectile : snapshot.projectiles()) {
            drawProjectile(graphics, projectile);
        }
        for (EnemySnapshot enemy : snapshot.enemies()) {
            drawEnemy(graphics, enemy, (int) canvas.getHeight());
        }
        for (PlayerSnapshot player : snapshot.players()) {
            drawPlayer(graphics, player, (int) canvas.getHeight());
        }
        graphics.restore();
        drawAimTarget(graphics);
    }

    public double worldX(double screenX) {
        return screenX + cameraX;
    }

    public double worldY(double screenY) {
        return screenY + cameraY;
    }

    public Position playerScreenPosition() {
        return playerScreenPosition;
    }

    public void aimTarget(Position target) {
        aimTarget = target;
    }

    private void updateCamera(GameSnapshot snapshot) {
        PlayerSnapshot followedPlayer = snapshot.players().stream()
                .filter(player -> Objects.equals(player.id(), snapshot.followedPlayerId()))
                .findFirst()
                .orElse(null);
        if (followedPlayer == null) {
            cameraX = 0;
            cameraY = 0;
            playerScreenPosition = new Position(canvas.getWidth() / 2, canvas.getHeight() / 2);
            return;
        }

        double centerX = followedPlayer.x() + followedPlayer.size() / 2.0;
        double centerY = followedPlayer.y() + followedPlayer.size() / 2.0;
        cameraX = clampCamera(centerX - canvas.getWidth() / 2, snapshot.worldWidth(), canvas.getWidth());
        cameraY = clampCamera(centerY - canvas.getHeight() / 2, snapshot.worldHeight(), canvas.getHeight());
        playerScreenPosition = new Position(centerX - cameraX, centerY - cameraY);
    }

    private void drawAimTarget(GraphicsContext graphics) {
        if (aimTarget == null) {
            return;
        }
        graphics.save();
        graphics.beginPath();
        graphics.rect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphics.clip();
        graphics.setStroke(Color.BLACK);
        graphics.setLineWidth(3);
        graphics.strokeOval(aimTarget.x() - 5, aimTarget.y() - 5, 10, 10);
        graphics.setStroke(Color.WHITE);
        graphics.setLineWidth(1);
        graphics.strokeOval(aimTarget.x() - 5, aimTarget.y() - 5, 10, 10);
        graphics.strokeLine(aimTarget.x() - 8, aimTarget.y(), aimTarget.x() + 8, aimTarget.y());
        graphics.strokeLine(aimTarget.x(), aimTarget.y() - 8, aimTarget.x(), aimTarget.y() + 8);
        graphics.restore();
    }

    private static double clampCamera(double desiredOffset, double worldSize, double viewportSize) {
        return Math.max(0, Math.min(worldSize - viewportSize, desiredOffset));
    }

    private void drawEnemy(GraphicsContext graphics, EnemySnapshot enemy, int canvasHeight) {
        double x = enemy.x();
        double y = enemy.y();
        double size = enemy.size();

        graphics.setImageSmoothing(false);
        graphics.drawImage(spriteCatalog.enemySprite(enemy.type()), x, y, size, size);
        for (var accessory : enemy.accessories()) {
            graphics.drawImage(spriteCatalog.enemyAccessorySprite(accessory), x, y, size, size);
        }

        
        drawHealthBar(
            graphics,
            x,
            y,
            size,
            enemy.currentHealth(),
            enemy.maxHealth(),
            canvasHeight,
            cameraY
        );
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
        int firstX = Math.max(0, (int) cameraX / TILE_SIZE * TILE_SIZE);
        int firstY = Math.max(0, (int) cameraY / TILE_SIZE * TILE_SIZE);
        int lastX = Math.min(width, (int) Math.ceil((cameraX + canvas.getWidth()) / TILE_SIZE) * TILE_SIZE);
        int lastY = Math.min(height, (int) Math.ceil((cameraY + canvas.getHeight()) / TILE_SIZE) * TILE_SIZE);
        for (int y = firstY; y < lastY; y += TILE_SIZE) {
            for (int x = firstX; x < lastX; x += TILE_SIZE) {
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
                canvasHeight,
                cameraY
        );
    }

    private void drawHealthBar(
            GraphicsContext graphics,
            double x,
            double y,
            double size,
            int currentHealth,
            int maxHealth,
            int canvasHeight,
            double cameraY
    ) {
        double barY = y + size + 3;
        if (barY - cameraY + 4 > canvasHeight) {
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
