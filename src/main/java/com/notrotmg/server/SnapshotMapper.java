package com.notrotmg.server;

import com.notrotmg.domain.GameRules;
import com.notrotmg.domain.Player;
import com.notrotmg.domain.item.Equipment;
import com.notrotmg.domain.item.EquipmentSlot;
import com.notrotmg.domain.item.Inventory;
import com.notrotmg.domain.item.Item;
import com.notrotmg.domain.item.ItemSlot;
import com.notrotmg.protocol.servertoclient.GameSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySlotSnapshot;
import com.notrotmg.protocol.servertoclient.InventorySnapshot;
import com.notrotmg.protocol.servertoclient.ItemSnapshot;
import com.notrotmg.protocol.servertoclient.PlayerSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Converts authoritative domain objects into immutable network messages. */
public final class SnapshotMapper {
    public GameSnapshot gameSnapshot(Collection<Player> players) {
        List<PlayerSnapshot> playerSnapshots = players.stream()
                .map(this::playerSnapshot)
                .toList();
        return new GameSnapshot(
                GameRules.CANVAS_WIDTH,
                GameRules.CANVAS_HEIGHT,
                playerSnapshots
        );
    }

    public InventorySnapshot inventorySnapshot(Inventory inventory) {
        List<InventorySlotSnapshot> inventorySlots = new ArrayList<>();
        List<ItemSlot> slots = inventory.itemSlots();
        for (int index = 0; index < slots.size(); index++) {
            ItemSnapshot item = slots.get(index).item()
                    .map(this::itemSnapshot)
                    .orElse(null);
            inventorySlots.add(new InventorySlotSnapshot(index, item));
        }

        Map<EquipmentSlot, ItemSnapshot> equippedItems = new EnumMap<>(EquipmentSlot.class);
        for (Map.Entry<EquipmentSlot, Equipment> entry : inventory.equippedItems().entrySet()) {
            equippedItems.put(entry.getKey(), itemSnapshot(entry.getValue()));
        }
        return new InventorySnapshot(inventorySlots, equippedItems);
    }

    private PlayerSnapshot playerSnapshot(Player player) {
        return new PlayerSnapshot(
                player.id(),
                (int) Math.round(player.position().x()),
                (int) Math.round(player.position().y()),
                (int) Math.round(player.size()),
                player.level(),
                player.currentHealth(),
                player.stats().maxHealth(),
                player.characterClass()
        );
    }

    private ItemSnapshot itemSnapshot(Item item) {
        EquipmentSlot equipmentSlot = item instanceof Equipment equipment
                ? equipment.slot()
                : null;
        return new ItemSnapshot(item.name(), item.type(), equipmentSlot);
    }
}
