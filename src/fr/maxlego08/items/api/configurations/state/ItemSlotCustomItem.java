package fr.maxlego08.items.api.configurations.state;

import fr.maxlego08.items.api.FakeItem;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;

public class ItemSlotCustomItem implements ItemSlot {
    private final ItemPlugin plugin;
    private final int slot;
    private final String customItem;
    private final int amount;

    public ItemSlotCustomItem(ItemPlugin plugin, int slot, String customItem, int amount) {
        this.plugin = plugin;
        this.slot = slot;
        this.customItem = customItem;
        this.amount = amount;
    }

    @Override
    public int slot() {
        return this.slot;
    }

    @Override
    public Item item() {
        var optional = this.plugin.getItemManager().getItem(customItem);
        return optional.orElse(new FakeItem());
    }

    @Override
    public int amount() {
        return this.amount;
    }
}