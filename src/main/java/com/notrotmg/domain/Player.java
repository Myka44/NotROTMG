package com.notrotmg.domain;

import com.notrotmg.domain.item.Equipment;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.Inventory;
import com.notrotmg.domain.item.Item;

import java.util.Objects;
import java.util.Optional;

public final class Player extends Entity implements Movable {
    private final int spawnSlot;
    private final CharacterClass characterClass;
    private final Inventory inventory = new Inventory();
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

    public Inventory inventory() {
        return inventory;
    }

    public void addItem(Item item) {
        if (!inventory.store(item)) {
            throw new IllegalStateException("Player inventory is full");
        }
    }

    public Optional<Equipment> equipFromInventorySlot(int inventorySlot) {
        Item item = inventory.itemInSlot(inventorySlot)
                .orElseThrow(() -> new IllegalStateException("Cannot equip an empty inventory slot"));
        if (!(item instanceof Equipment equipment)) {
            throw new IllegalArgumentException("The selected item is not equipment");
        }
        if (!canEquip(equipment)) {
            throw new IllegalArgumentException(
                    characterClass + " cannot equip " + equipment.type()
            );
        }
        return inventory.equipFromSlot(inventorySlot);
    }

    public boolean unequip(EquipmentSlot equipmentSlot) {
        return inventory.unequip(Objects.requireNonNull(equipmentSlot));
    }

    public boolean dropFromInventorySlot(int inventorySlot) {
        return inventory.takeFromSlot(inventorySlot).isPresent();
    }

    public int level() {
        return level;
    }

    public long experience() {
        return experience;
    }
}
