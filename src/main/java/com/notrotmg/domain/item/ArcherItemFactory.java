package com.notrotmg.domain.item;

public final class ArcherItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon("Longbow", ItemType.BOW, 15);
    }

    @Override
    public Armor createArmor() {
        return new Armor("Leather Armor", ItemType.LEATHER_ARMOR, EquipmentSlot.ARMOR, 8);
    }

    @Override
    public Spell createSpell() {
        return new Spell("Piercing Shot", 16, 12);
    }

    @Override
    public Armor createRing() {
        return new Armor("Hunter's Ring", ItemType.RING, EquipmentSlot.RING, 3);
    }
}
