package com.notrotmg.domain.item;

import com.notrotmg.domain.projectile.ActiveProjectile;
import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.ProjectileDefinition;

import java.util.List;
import java.util.Objects;

public final class Weapon extends Equipment {
    private final ProjectileDefinition projectileDefinition;
    private final AttackStrategy attackStrategy;

    public Weapon(
            String name,
            ItemType type,
            ProjectileDefinition projectileDefinition,
            AttackStrategy attackStrategy
    ) {
        super(name, requireWeaponType(type), EquipmentSlot.WEAPON);
        this.projectileDefinition = Objects.requireNonNull(projectileDefinition);
        this.attackStrategy = Objects.requireNonNull(attackStrategy);
    }

    public int damageBonus() {
        return projectileDefinition.damage();
    }

    public List<ActiveProjectile> attack(AttackContext context) {
        return attackStrategy.attack(Objects.requireNonNull(context), projectileDefinition);
    }

    public ProjectileDefinition projectileDefinition() {
        return projectileDefinition;
    }

    public AttackStrategy attackStrategy() {
        return attackStrategy;
    }

    private static ItemType requireWeaponType(ItemType type) {
        if (type != ItemType.SWORD && type != ItemType.STAFF && type != ItemType.BOW) {
            throw new IllegalArgumentException("A weapon must have a weapon item type");
        }
        return type;
    }
}
