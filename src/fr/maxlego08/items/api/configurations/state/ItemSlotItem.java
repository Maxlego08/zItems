package fr.maxlego08.items.api.configurations.state;

import fr.maxlego08.items.api.Item;
import org.bukkit.entity.Player;

public record ItemSlotItem(int slot, Item item) implements ItemSlot {
    @Override
    public Item item(Player player) {
        return this.item;
    }

    @Override
    public int amount() {
        return 0;
    }
}