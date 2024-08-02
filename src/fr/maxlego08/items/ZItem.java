package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ZItem implements Item {

    private final ItemsPlugin plugin;
    private final ItemConfiguration configuration;

    public ZItem(ItemsPlugin plugin, ItemConfiguration configuration) {
        this.plugin = plugin;
        this.configuration = configuration;
    }

    @Override
    public ItemConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public ItemStack build(Player player) {
        ItemStack itemStack = new ItemStack(this.configuration.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            itemMeta.setMaxStackSize(this.configuration.getMaxStackSize());

            itemMeta.setItemName(this.configuration.getItemName());
            itemMeta.setDisplayName(this.configuration.getCustomName().replace("&", "§"));
            itemMeta.setLore(this.configuration.getLore().stream().map(e -> e.replace("&", "§")).toList());

            if (itemMeta instanceof Damageable damageable) {
                damageable.setMaxDamage(this.configuration.getMaxDamage());
                damageable.setDamage(this.configuration.getDamage());
                damageable.setUnbreakable(this.configuration.isUnbreakableEnabled());
            }

            if (itemMeta instanceof Repairable repairable) {
                repairable.setRepairCost(this.configuration.getRepairCost());
            }

            itemStack.setItemMeta(itemMeta);
        } else {
            plugin.getLogger().severe("ItemMeta is null !");
        }

        return itemStack;
    }
}
