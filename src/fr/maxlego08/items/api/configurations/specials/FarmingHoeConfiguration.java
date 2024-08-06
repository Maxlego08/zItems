package fr.maxlego08.items.api.configurations.specials;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public record FarmingHoeConfiguration(int size) implements SpecialConfiguration {

    public static final NamespacedKey FARMING_HOE_SIZE_KEY = new NamespacedKey(JavaPlugin.getProvidingPlugin(ItemPlugin.class), "farming-hoe-size");

    @Override
    public void apply(Player player, ItemPlugin plugin, ItemMeta itemMeta, PersistentDataContainer persistentDataContainer) {

        persistentDataContainer.set(FARMING_HOE_SIZE_KEY, PersistentDataType.INTEGER, this.size);

    }
}
