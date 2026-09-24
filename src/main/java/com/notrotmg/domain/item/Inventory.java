package com.notrotmg.domain.item;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Inventory {
    public static final int DEFAULT_CAPACITY = 8;

    private final List<ItemSlot> itemSlots;
    private final EnumMap<EquipmentSlot, Equipment> equippedItems = new EnumMap<>(EquipmentSlot.class);

    public Inventory() {
        this(DEFAULT_CAPACITY);
    }

    public Inventory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Inventory capacity must be positive");
        }
        itemSlots = new ArrayList<>(capacity);
        for (int index = 0; index < capacity; index++) {
            itemSlots.add(new ItemSlot());
        }
    }

    public boolean store(Item item) {
        Objects.requireNonNull(item);
        Optional<ItemSlot> emptySlot = itemSlots.stream()
                .filter(ItemSlot::isEmpty)
                .findFirst();
        emptySlot.ifPresent(slot -> slot.store(item));
        return emptySlot.isPresent();
    }

    public Optional<Item> takeFromSlot(int index) {
        return slotAt(index).take();
    }

    public Optional<Equipment> equipFromSlot(int index) {
        ItemSlot itemSlot = slotAt(index);
        Item item = itemSlot.item()
                .orElseThrow(() -> new IllegalStateException("Cannot equip an empty item slot"));
        if (!(item instanceof Equipment equipment)) {
            throw new IllegalArgumentException("Only equipment can be equipped");
        }

        itemSlot.take();
        Equipment replaced = equippedItems.put(equipment.slot(), equipment);
        if (replaced != null) {
            itemSlot.store(replaced);
        }
        return Optional.ofNullable(replaced);
    }

    public boolean unequip(EquipmentSlot equipmentSlot) {
        Equipment equipment = equippedItems.get(equipmentSlot);
        if (equipment == null) {
            return false;
        }

        Optional<ItemSlot> emptySlot = itemSlots.stream()
                .filter(ItemSlot::isEmpty)
                .findFirst();
        if (emptySlot.isEmpty()) {
            return false;
        }

        equippedItems.remove(equipmentSlot);
        emptySlot.orElseThrow().store(equipment);
        return true;
    }

    public List<ItemSlot> itemSlots() {
        return List.copyOf(itemSlots);
    }

    public Map<EquipmentSlot, Equipment> equippedItems() {
        return Map.copyOf(equippedItems);
    }

    private ItemSlot slotAt(int index) {
        if (index < 0 || index >= itemSlots.size()) {
            throw new IndexOutOfBoundsException("Inventory slot index: " + index);
        }
        return itemSlots.get(index);
    }
}
