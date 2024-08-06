package fr.maxlego08.items.specials;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.configurations.specials.SpecialConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public abstract class SpecialHelper<T extends SpecialConfiguration> {

    protected final ItemsPlugin plugin;
    protected final ItemType itemType;

    public SpecialHelper(ItemsPlugin plugin, ItemType itemType) {
        this.plugin = plugin;
        this.itemType = itemType;
    }

    public boolean isSpecialItem(ItemStack itemStack) {

        if (itemStack == null || !itemStack.hasItemMeta()) return false;

        var itemMeta = itemStack.getItemMeta();
        var persistentDataContainer = itemMeta.getPersistentDataContainer();

        if (!persistentDataContainer.has(Item.ITEM_KEY, PersistentDataType.STRING)) return false;

        String itemName = persistentDataContainer.get(Item.ITEM_KEY, PersistentDataType.STRING);
        var optional = plugin.getItemManager().getItem(itemName);
        if (optional.isPresent()) {
            var item = optional.get();
            return item.getConfiguration().getItemType() == this.itemType;
        }
        return false;
    }

    public T getSpecialConfiguration(ItemStack itemStack) {

        var configuration = getConfiguration(itemStack);
        return (T) configuration.getSpecialConfiguration();
    }

    public ItemConfiguration getConfiguration(ItemStack itemStack) {

        var itemMeta = itemStack.getItemMeta();
        var persistentDataContainer = itemMeta.getPersistentDataContainer();
        String itemName = persistentDataContainer.get(Item.ITEM_KEY, PersistentDataType.STRING);
        var item = plugin.getItemManager().getItem(itemName).get();

        return item.getConfiguration();
    }
}
