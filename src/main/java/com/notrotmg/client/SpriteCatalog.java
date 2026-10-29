package com.notrotmg.client;

import com.notrotmg.domain.CharacterClass;
import com.notrotmg.domain.item.ItemType;
import com.notrotmg.domain.enemy.EnemyType;
import com.notrotmg.domain.enemy.RegularEnemyAccessory;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Loads the sprite sheet and caches the character, enemy, and item sprites. */
public final class SpriteCatalog {
    private static final String SPRITE_SHEET = "/sprites/ProjectUtumno_full.png";
    private static final int ATLAS_COLUMNS = 64;
    private static final int ATLAS_ROWS = 95;
    private static final int VISIBLE_ALPHA_THRESHOLD = 16;

    private final Map<ItemType, Image> itemSprites = new EnumMap<>(ItemType.class);
    private final Map<String, Image> namedItemSprites = new HashMap<>();
    private final Map<CharacterClass, Image> playerSprites = new EnumMap<>(CharacterClass.class);
    private final Map<EnemyType, Image> enemySprites = new EnumMap<>(EnemyType.class);
    private final Map<RegularEnemyAccessory, Image> enemyAccessorySprites =
            new EnumMap<>(RegularEnemyAccessory.class);


    public SpriteCatalog() {
        Image atlas = loadSpriteSheet();
        PixelReader pixels = Objects.requireNonNull(atlas.getPixelReader(), "Sprite sheet has no pixels");

        playerSprites.put(CharacterClass.WARRIOR, crop(atlas, pixels, 50, 76));
        playerSprites.put(CharacterClass.WIZARD, crop(atlas, pixels, 49, 76));
        playerSprites.put(CharacterClass.ARCHER, crop(atlas, pixels, 47, 77));

        enemySprites.put(EnemyType.REGULAR, crop(atlas, pixels, 61, 79));
        enemySprites.put(EnemyType.BOSS, crop(atlas, pixels, 1, 94));

        enemyAccessorySprites.put(RegularEnemyAccessory.ARMOR, cropCell(atlas, pixels, 57, 82));
        enemyAccessorySprites.put(RegularEnemyAccessory.SWORD, cropCell(atlas, pixels, 8, 87));
        enemyAccessorySprites.put(RegularEnemyAccessory.BOOTS, cropCell(atlas, pixels, 57, 83));

        itemSprites.put(ItemType.SWORD, cropCell(atlas, pixels, 4, 45));
        itemSprites.put(ItemType.STAFF, cropCell(atlas, pixels, 5, 44));
        itemSprites.put(ItemType.BOW, cropCell(atlas, pixels, 6, 48));
        itemSprites.put(ItemType.ROBE, cropCell(atlas, pixels, 1, 34));
        itemSprites.put(ItemType.LEATHER_ARMOR, cropCell(atlas, pixels, 24, 38));
        itemSprites.put(ItemType.HEAVY_ARMOR, cropCell(atlas, pixels, 20, 38));
        itemSprites.put(ItemType.HELMET, cropCell(atlas, pixels, 2, 37));
        itemSprites.put(ItemType.RING, cropCell(atlas, pixels, 6, 43));
        itemSprites.put(ItemType.SPELL, cropCell(atlas, pixels, 0, 39));
        itemSprites.put(ItemType.POTION, cropCell(atlas, pixels, 0, 42));

        namedItemSprites.put("Iron Ring", cropCell(atlas, pixels, 15, 43));
        namedItemSprites.put("Arcane Ring", cropCell(atlas, pixels, 13, 43));
        namedItemSprites.put("Hunter's Ring", cropCell(atlas, pixels, 18, 43));
        namedItemSprites.put("Fireball", cropCell(atlas, pixels, 18, 31));
        namedItemSprites.put("Piercing Shot", cropCell(atlas, pixels, 23, 30));
    }

    public Image itemSprite(ItemType itemType) {
        Image sprite = itemSprites.get(Objects.requireNonNull(itemType));
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite registered for item type: " + itemType);
        }
        return sprite;
    }

    public Image itemSprite(ItemType itemType, String itemName) {
        Image sprite = namedItemSprites.get(Objects.requireNonNull(itemName));
        return sprite != null ? sprite : itemSprite(itemType);
    }

    public Image playerSprite(CharacterClass characterClass) {
        Image sprite = playerSprites.get(Objects.requireNonNull(characterClass));
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite registered for character class: " + characterClass);
        }
        return sprite;
    }

    public Image enemySprite(EnemyType enemyType) {
        Image sprite = enemySprites.get(Objects.requireNonNull(enemyType));
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite registered for character class: " + enemyType);
        }
        return sprite;
    }

    public Image enemyAccessorySprite(RegularEnemyAccessory accessory) {
        Image sprite = enemyAccessorySprites.get(Objects.requireNonNull(accessory));
        if (sprite == null) {
            throw new IllegalArgumentException("No sprite registered for enemy accessory: " + accessory);
        }
        return sprite;
    }

    private Image loadSpriteSheet() {
        URL resource = Objects.requireNonNull(
                getClass().getResource(SPRITE_SHEET),
                "Missing sprite sheet: " + SPRITE_SHEET
        );
        Image image = new Image(resource.toExternalForm(), false);
        if (image.isError()) {
            throw new IllegalStateException("Could not load sprite sheet: " + SPRITE_SHEET, image.getException());
        }
        return image;
    }

    private static Image crop(
            Image atlas,
            PixelReader pixels,
            int column,
            int row
    ) {
        return extractDominantSprite(cropCell(atlas, pixels, column, row));
    }

    private static Image cropCell(
            Image atlas,
            PixelReader pixels,
            int column,
            int row
    ) {
        int x = boundary(column, (int) atlas.getWidth(), ATLAS_COLUMNS);
        int y = boundary(row, (int) atlas.getHeight(), ATLAS_ROWS);
        int nextX = boundary(column + 1, (int) atlas.getWidth(), ATLAS_COLUMNS);
        int nextY = boundary(row + 1, (int) atlas.getHeight(), ATLAS_ROWS);
        return new WritableImage(
                pixels,
                x,
                y,
                nextX - x,
                nextY - y
        );
    }

    private static int boundary(int index, int imageSize, int cellCount) {
        return (int) Math.round((double) index * imageSize / cellCount);
    }

    private static Image extractDominantSprite(Image image) {
        PixelReader pixels = Objects.requireNonNull(image.getPixelReader());
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        boolean[] visiblePixels = new boolean[width * height];
        boolean[] visited = new boolean[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = y * width + x;
                int alpha = pixels.getArgb(x, y) >>> 24;
                visiblePixels[index] = alpha > VISIBLE_ALPHA_THRESHOLD;
            }
        }

        List<Integer> dominantComponent = List.of();
        ArrayDeque<Integer> pendingPixels = new ArrayDeque<>();
        for (int index = 0; index < visiblePixels.length; index++) {
            if (!visiblePixels[index] || visited[index]) {
                continue;
            }

            List<Integer> component = new ArrayList<>();
            visited[index] = true;
            pendingPixels.add(index);
            while (!pendingPixels.isEmpty()) {
                int current = pendingPixels.removeFirst();
                component.add(current);
                int currentX = current % width;
                int currentY = current / width;

                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        if (offsetX == 0 && offsetY == 0) {
                            continue;
                        }
                        int neighborX = currentX + offsetX;
                        int neighborY = currentY + offsetY;
                        if (neighborX < 0 || neighborX >= width
                                || neighborY < 0 || neighborY >= height) {
                            continue;
                        }
                        int neighbor = neighborY * width + neighborX;
                        if (visiblePixels[neighbor] && !visited[neighbor]) {
                            visited[neighbor] = true;
                            pendingPixels.addLast(neighbor);
                        }
                    }
                }
            }

            if (component.size() > dominantComponent.size()) {
                dominantComponent = component;
            }
        }

        if (dominantComponent.isEmpty()) {
            return new WritableImage(1, 1);
        }

        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;

        for (int pixel : dominantComponent) {
            int x = pixel % width;
            int y = pixel / width;

            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }

        int contentWidth = maxX - minX + 1;
        int contentHeight = maxY - minY + 1;

        int spriteSize = Math.max(contentWidth, contentHeight);

        int destinationX = (spriteSize - contentWidth) / 2;
        int destinationY = (spriteSize - contentHeight) / 2;

        WritableImage trimmed = new WritableImage(spriteSize, spriteSize);
        PixelWriter writer = trimmed.getPixelWriter();

        for (int pixel : dominantComponent) {
            int sourceX = pixel % width;
            int sourceY = pixel / width;

            writer.setArgb(
                destinationX + sourceX - minX,
                destinationY + sourceY - minY,
                pixels.getArgb(sourceX, sourceY)
            );
        }

        return trimmed;
    }
}
