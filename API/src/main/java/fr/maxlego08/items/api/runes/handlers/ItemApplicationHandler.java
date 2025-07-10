package fr.maxlego08.items.api.runes.handlers;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.inventory.meta.ItemMeta;

public interface ItemApplicationHandler<T extends RuneConfiguration> {

    void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, T runeConfiguration) throws Exception;

}
