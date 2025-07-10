package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.entity.Player;

public interface InventorySlotChangeHandler<T extends RuneConfiguration> {

    enum InventorySlotChangeType {
        EQUIP,
        UNEQUIP
    }

    InventorySlotChangeHandler.InventorySlotChangeType getType(T configuration);

    void onInventorySlotChange(ItemPlugin plugin, Player player, T runeConfiguration);
}
