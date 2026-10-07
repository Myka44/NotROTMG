package com.notrotmg.domain.item;

/** Abstract Factory for a related family of class-specific items. */
public interface CharacterItemFactory {
    Weapon createWeapon();

    Armor createArmor();

    Equipment createAbility();

    Armor createRing();
}
