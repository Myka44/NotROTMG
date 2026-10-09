package com.notrotmg.domain.item;

import com.notrotmg.domain.projectile.AttackContext;
import com.notrotmg.domain.projectile.AttackStrategy;
import com.notrotmg.domain.projectile.Projectile;
import com.notrotmg.domain.projectile.ProjectileBuilder;
import com.notrotmg.domain.projectile.ProjectileSpec;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public final class Weapon extends Equipment {
    private final ProjectileSpec projectileSpec;
    private final AttackStrategy attackStrategy;
    private final Supplier<ProjectileBuilder> projectileBuilderFactory;

    public Weapon(
            String name,
            ItemType type,
            ProjectileSpec projectileSpec,
            AttackStrategy attackStrategy,
            Supplier<ProjectileBuilder> projectileBuilderFactory
    ) {
        super(name, requireWeaponType(type), EquipmentSlot.WEAPON);
        this.projectileSpec = Objects.requireNonNull(projectileSpec);
        this.attackStrategy = Objects.requireNonNull(attackStrategy);
        this.projectileBuilderFactory = Objects.requireNonNull(projectileBuilderFactory);
    }

    public int damageBonus() {
        return projectileSpec.damage();
    }

    public List<Projectile> attack(AttackContext context) {
        return attackStrategy.attack(
                Objects.requireNonNull(context),
                projectileSpec,
                projectileBuilderFactory
        );
    }

    public ProjectileSpec projectileSpec() {
        return projectileSpec;
    }

    public AttackStrategy attackStrategy() {
        return attackStrategy;
    }

    public Supplier<ProjectileBuilder> projectileBuilderFactory() {
        return projectileBuilderFactory;
    }

    private static ItemType requireWeaponType(ItemType type) {
        if (type != ItemType.SWORD && type != ItemType.STAFF && type != ItemType.BOW) {
            throw new IllegalArgumentException("A weapon must have a weapon item type");
        }
        return type;
    }
}
