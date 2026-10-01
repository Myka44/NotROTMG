package com.notrotmg.domain.item;

public final class WizardItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon("Apprentice Staff", ItemType.STAFF, 8);
    }

    @Override
    public Armor createArmor() {
        return new Armor("Wizard Robe", ItemType.ROBE, EquipmentSlot.ARMOR, 5);
    }

    @Override
    public Spell createSpell() {
        return new Spell("Fireball", 24, 20);
    }

    @Override
    public Armor createRing() {
        return new Armor("Arcane Ring", ItemType.RING, EquipmentSlot.RING, 2);
    }
}
