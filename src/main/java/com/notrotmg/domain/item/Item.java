package com.notrotmg.domain.item;

import java.util.Objects;

public abstract class Item {
    private final String name;
    private final ItemType type;

    protected Item(String name, ItemType type) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name cannot be blank");
        }
        this.name = name;
        this.type = Objects.requireNonNull(type);
    }

    public final String name() {
        return name;
    }

    public final ItemType type() {
        return type;
    }
}
