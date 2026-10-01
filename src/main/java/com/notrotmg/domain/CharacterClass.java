package com.notrotmg.domain;

import com.notrotmg.domain.item.ItemType;

import java.util.Objects;
import java.util.Set;

public enum CharacterClass {
    WARRIOR(
            new EntityStats(150, 40, 20, 12, 180, 5),
            Set.of(
                    ItemType.SWORD,
                    ItemType.HEAVY_ARMOR,
                    ItemType.HELMET,
                    ItemType.SPELL,
                    ItemType.RING
            )
    ),
    WIZARD(
            new EntityStats(90, 150, 8, 5, 180, 24),
            Set.of(
                    ItemType.STAFF,
                    ItemType.ROBE,
                    ItemType.SPELL,
                    ItemType.RING
            )
    ),
    ARCHER(
            new EntityStats(110, 70, 17, 8, 200, 8),
            Set.of(
                    ItemType.BOW,
                    ItemType.LEATHER_ARMOR,
                    ItemType.HELMET,
                    ItemType.SPELL,
                    ItemType.RING
            )
    );

    private final EntityStats baseStats;
    private final Set<ItemType> allowedEquipmentTypes;

    CharacterClass(EntityStats baseStats, Set<ItemType> allowedEquipmentTypes) {
        this.baseStats = Objects.requireNonNull(baseStats);
        this.allowedEquipmentTypes = Set.copyOf(allowedEquipmentTypes);
    }

    public EntityStats baseStats() {
        return baseStats;
    }

    public Set<ItemType> allowedEquipmentTypes() {
        return allowedEquipmentTypes;
    }

    public boolean canEquip(ItemType itemType) {
        return allowedEquipmentTypes.contains(Objects.requireNonNull(itemType));
    }
}
