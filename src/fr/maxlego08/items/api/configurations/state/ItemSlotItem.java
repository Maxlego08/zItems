package fr.maxlego08.items.api.configurations.state;

import fr.maxlego08.items.api.Item;

public record ItemSlotItem(int slot, Item item) implements ItemSlot {
    @Override
    public Item item() {
        return this.item;
    }

    @Override
    public int amount() {
        return 0;
    }
}