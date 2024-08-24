package fr.maxlego08.items.runes.activators;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.inventory.meta.ItemMeta;

public class Unbreakable extends RuneActivatorHelper<RuneConfiguration> {

    @Override
    public void applyOnItems(ItemPlugin plugin, ItemMeta itemMeta, RuneConfiguration runeConfiguration) throws Exception {
        itemMeta.setUnbreakable(true);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
