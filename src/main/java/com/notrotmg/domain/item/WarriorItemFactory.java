package com.notrotmg.domain.item;

public final class WarriorItemFactory implements CharacterItemFactory {
    @Override
    public Weapon createWeapon() {
        return new Weapon("Iron Sword", ItemType.SWORD, 18);
    }

    @Override
    public Armor createArmor() {
        return new Armor("Heavy Plate", ItemType.HEAVY_ARMOR, EquipmentSlot.ARMOR, 16);
    }

    @Override
    public Spell createSpell() {
        return new Spell("Battle Cry", 10, 8);
    }

    @Override
    public Armor createRing() {
        return new Armor("Iron Ring", ItemType.RING, EquipmentSlot.RING, 4);
    }
}
