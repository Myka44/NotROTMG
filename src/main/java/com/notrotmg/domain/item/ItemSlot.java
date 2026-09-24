package com.notrotmg.domain.item;

import java.util.Objects;
import java.util.Optional;

public final class ItemSlot {
    private Item item;

    public boolean isEmpty() {
        return item == null;
    }

    public Optional<Item> item() {
        return Optional.ofNullable(item);
    }

    public void store(Item item) {
        if (!isEmpty()) {
            throw new IllegalStateException("Item slot is already occupied");
        }
        this.item = Objects.requireNonNull(item);
    }

    public Optional<Item> take() {
        Item storedItem = item;
        item = null;
        return Optional.ofNullable(storedItem);
    }
}
