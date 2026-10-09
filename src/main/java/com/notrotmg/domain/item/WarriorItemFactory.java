package com.notrotmg.domain.item;

import com.notrotmg.domain.projectile.KineticProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;
import com.notrotmg.domain.projectile.SingleShotStrategy;

public final class WarriorItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon(
                "Iron Sword",
                ItemType.SWORD,
                new ProjectileSpec("iron-sword-projectile", 18, 420, 8, 0.8),
                new SingleShotStrategy(),
                () -> new KineticProjectileBuilder(3)
        );
    }

    @Override
    public Armor createArmor() {
        return new Armor("Heavy Plate", ItemType.HEAVY_ARMOR, EquipmentSlot.ARMOR, 16);
    }

    @Override
    public Equipment createAbility() {
        return new Armor("Iron Helmet", ItemType.HELMET, EquipmentSlot.ABILITY, 6);
    }

    @Override
    public Armor createRing() {
        return new Armor("Iron Ring", ItemType.RING, EquipmentSlot.RING, 4);
    }
}
