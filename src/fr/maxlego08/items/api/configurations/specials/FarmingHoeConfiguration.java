package fr.maxlego08.items.api.configurations.specials;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public record FarmingHoeConfiguration(int size, boolean autoPlant, DropItemType dropItemType, boolean dropItemInInventory) implements SpecialConfiguration {


    @Override
    public void apply(Player player, ItemPlugin plugin, ItemMeta itemMeta, PersistentDataContainer persistentDataContainer) {


    }

    public enum DropItemType {
        BLOCK,
        CENTER,
        PLAYER
    }
}
