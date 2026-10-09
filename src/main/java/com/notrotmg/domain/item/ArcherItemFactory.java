package com.notrotmg.domain.item;

import com.notrotmg.domain.projectile.KineticProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.SingleShotStrategy;

public final class ArcherItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon(
                "Longbow",
                ItemType.BOW,
                new ProjectileSpec("arrow-projectile", 15, 520, 6, 1.2),
                new SingleShotStrategy(),
                () -> new KineticProjectileBuilder(2)
        );
    }

    @Override
    public Armor createArmor() {
        return new Armor("Leather Armor", ItemType.LEATHER_ARMOR, EquipmentSlot.ARMOR, 8);
    }

    @Override
    public Equipment createAbility() {
        return new Spell("Piercing Shot", 16, 12);
    }

    @Override
    public Armor createRing() {
        return new Armor("Hunter's Ring", ItemType.RING, EquipmentSlot.RING, 3);
    }
}
