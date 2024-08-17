package fr.maxlego08.items.api.configurations.specials;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public record VeinMiningConfiguration(
        int blockLimit
) implements SpecialConfiguration {

    @Override
    public void apply(Player player, ItemPlugin plugin, ItemMeta itemMeta, PersistentDataContainer persistentDataContainer) {

    }
}
