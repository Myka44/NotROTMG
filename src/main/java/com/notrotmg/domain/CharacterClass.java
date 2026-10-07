package com.notrotmg.domain;

import com.notrotmg.domain.item.ItemType;

import java.util.Objects;
import java.util.Set;

public enum CharacterClass {
    WARRIOR(
            EntityStats.builder()
                    .maxHealth(150)
                    .maxMana(40)
                    .damage(20)
                    .defense(12)
                    .speed(180)
                    .spellStrength(5)
                    .build(),
            Set.of(
                    ItemType.SWORD,
                    ItemType.HEAVY_ARMOR,
                    ItemType.HELMET,
                    ItemType.SPELL,
                    ItemType.RING
            )
    ),
    WIZARD(
            EntityStats.builder()
                    .maxHealth(90)
                    .maxMana(150)
                    .damage(8)
                    .defense(5)
                    .speed(180)
                    .spellStrength(24)
                    .build(),
            Set.of(
                    ItemType.STAFF,
                    ItemType.ROBE,
                    ItemType.SPELL,
                    ItemType.RING
            )
    ),
    ARCHER(
            EntityStats.builder()
                    .maxHealth(110)
                    .maxMana(70)
                    .damage(17)
                    .defense(8)
                    .speed(200)
                    .spellStrength(8)
                    .build(),
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
