package com.notrotmg.domain;

import com.notrotmg.domain.item.Equipment;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.Inventory;
import com.notrotmg.domain.item.Item;
import com.notrotmg.domain.item.Weapon;
import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.Projectile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class Player extends Entity implements Movable {
    private final int spawnSlot;
    private final CharacterClass characterClass;
    private final Inventory inventory = new Inventory();
    private Vector2 movementDirection = Vector2.ZERO;
    private Position attackTarget;
    private boolean firing;
    private double remainingAttackCooldown;
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

    public void attackInput(Position target, boolean firing) {
        attackTarget = Objects.requireNonNull(target);
        this.firing = firing;
    }

    public List<Projectile> updateAttack(double deltaSeconds) {
        if (deltaSeconds < 0) {
            throw new IllegalArgumentException("Delta time cannot be negative");
        }
        remainingAttackCooldown = Math.max(0, remainingAttackCooldown - deltaSeconds);
        if (!firing || attackTarget == null || remainingAttackCooldown > 0) {
            return List.of();
        }

        Optional<Weapon> equippedWeapon = inventory.equippedWeapon();
        if (equippedWeapon.isEmpty()) {
            return List.of();
        }

        Position origin = new Position(
                position().x() + size() / 2,
                position().y() + size() / 2
        );
        Vector2 aimDirection = new Vector2(
                attackTarget.x() - origin.x(),
                attackTarget.y() - origin.y()
        );
        if (aimDirection.equals(Vector2.ZERO)) {
            return List.of();
        }

        Weapon weapon = equippedWeapon.orElseThrow();
        remainingAttackCooldown = weapon.attackCooldownSeconds();
        return weapon.attack(new AttackContext(id(), origin, aimDirection));
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
