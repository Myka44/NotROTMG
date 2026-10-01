package com.notrotmg.domain;

import com.notrotmg.domain.item.Equipment;

import java.util.Objects;

public final class Player extends Entity implements Movable {
    private final int spawnSlot;
    private final CharacterClass characterClass;
    private Vector2 movementDirection = Vector2.ZERO;
    private int level = 1;
    private long experience;

    public Player(String id, int spawnSlot, Position position, CharacterClass characterClass) {
        super(id, position, GameRules.PLAYER_SIZE, characterClass.baseStats());
        this.spawnSlot = spawnSlot;
        this.characterClass = Objects.requireNonNull(characterClass);
    }

    public void movementDirection(Vector2 direction) {
        movementDirection = Objects.requireNonNull(direction).normalized();
    }

    @Override
    public void move(double deltaSeconds, WorldBounds bounds) {
        double distance = stats().speed() * deltaSeconds;
        position(bounds.clamp(position().translated(movementDirection, distance), size()));
    }

    public int spawnSlot() {
        return spawnSlot;
    }

    public CharacterClass characterClass() {
        return characterClass;
    }

    public boolean canEquip(Equipment equipment) {
        Objects.requireNonNull(equipment);
        return characterClass.canEquip(equipment.type());
    }

    public int level() {
        return level;
    }

    public long experience() {
        return experience;
    }
}
