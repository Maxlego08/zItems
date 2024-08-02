package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;

public class ZItem extends ZUtils implements Item {

    private final ItemsPlugin plugin;
    private final String name;
    private final ItemConfiguration configuration;

    public ZItem(ItemsPlugin plugin, String name, ItemConfiguration configuration) {
        this.plugin = plugin;
        this.name = name;
        this.configuration = configuration;
    }

    @Override
    public ItemConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack build(Player player, int amount) {

        ItemComponent itemComponent = this.plugin.getItemComponent();

        ItemStack itemStack = new ItemStack(this.configuration.getMaterial());
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            itemMeta.setMaxStackSize(this.configuration.getMaxStackSize());

            if (this.configuration.getItemName() != null) {
                itemComponent.setItemName(itemMeta, papi(this.configuration.getItemName(), player));
            }

            if (this.configuration.getDisplayName() != null) {
                itemComponent.setItemName(itemMeta, papi(this.configuration.getDisplayName(), player));
            }

            if (this.configuration.getLore() != null && !this.configuration.getLore().isEmpty()) {
                itemComponent.setLore(itemMeta, papi(this.configuration.getLore(), player));
            }

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
