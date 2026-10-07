package com.notrotmg.domain.item;

import com.notrotmg.domain.projectile.ProjectileDefinition;
import com.notrotmg.domain.projectile.SpreadShotStrategy;

public final class WizardItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon(
                "Apprentice Staff",
                ItemType.STAFF,
                new ProjectileDefinition("magic-bolt-projectile", 8, 380, 8, 1.4),
                new SpreadShotStrategy(3, 10)
        );
    }

    @Override
    public Armor createArmor() {
        return new Armor("Wizard Robe", ItemType.ROBE, EquipmentSlot.ARMOR, 5);
    }

    @Override
    public Equipment createAbility() {
        return new Spell("Fireball", 24, 20);
    }

    @Override
    public Armor createRing() {
        return new Armor("Arcane Ring", ItemType.RING, EquipmentSlot.RING, 2);
    }
}
