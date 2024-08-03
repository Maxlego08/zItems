package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.configurations.Food;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.UUID;

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

        ItemStack itemStack = new ItemStack(this.configuration.getMaterial());
        itemStack.setAmount(amount);
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            if (this.configuration.getMaxStackSize() > 0) {
                itemMeta.setMaxStackSize(this.configuration.getMaxStackSize());
            }

            this.applyNames(itemMeta, player);
            this.applyFood(itemMeta, player);
            this.applyTrim(itemMeta);

            if (itemMeta instanceof Damageable damageable) {
                if (this.configuration.getMaxDamage() > 0) damageable.setMaxDamage(this.configuration.getMaxDamage());
                damageable.setDamage(this.configuration.getDamage());
                damageable.setUnbreakable(this.configuration.isUnbreakableEnabled());

                if (!this.configuration.isUnbreakableShowInTooltip()) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
                }
            }

            if (itemMeta instanceof Repairable repairable) {
                repairable.setRepairCost(this.configuration.getRepairCost());
            }

            if (this.configuration.getCustomModelData() > 0) {
                itemMeta.setCustomModelData(this.configuration.getCustomModelData());
            }

            itemMeta.setHideTooltip(this.configuration.isHideTooltip());
            if (this.configuration.isHideAdditionalTooltip()) {
                for (ItemFlag value : ItemFlag.values()) {
                    itemMeta.addItemFlags(value);
                }
            }

            this.configuration.enchant(itemStack);
            if (!this.configuration.isEnchantmentShowInTooltip()) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            itemMeta.setEnchantmentGlintOverride(this.configuration.isEnchantmentGlint());

            this.configuration.getAttributes().forEach(attributeConfiguration -> itemMeta.addAttributeModifier(attributeConfiguration.attribute(), new AttributeModifier(NamespacedKey.fromString(UUID.randomUUID().toString()), attributeConfiguration.amount(), attributeConfiguration.operation(), attributeConfiguration.slot())));

            if (!this.configuration.isAttributeShowInTooltip()) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }

            itemStack.setItemMeta(itemMeta);
        } else {
            plugin.getLogger().severe("ItemMeta is null !");
        }

        return itemStack;
    }

    private void applyNames(ItemMeta itemMeta, Player player) {

        ItemComponent itemComponent = this.plugin.getItemComponent();

        if (this.configuration.getItemName() != null) {
            itemComponent.setItemName(itemMeta, papi(this.configuration.getItemName(), player));
        }

        if (this.configuration.getDisplayName() != null) {
            itemComponent.setItemName(itemMeta, papi(this.configuration.getDisplayName(), player));
        }

        if (this.configuration.getLore() != null && !this.configuration.getLore().isEmpty()) {
            itemComponent.setLore(itemMeta, papi(this.configuration.getLore(), player));
        }
    }

    private void applyFood(ItemMeta itemMeta, Player player) {

        Food food = this.configuration.getFood();
        if (food == null || !food.enable()) return;

        food.applyToItemMeta(itemMeta, player, this.plugin);
    }

    private void applyTrim(ItemMeta itemMeta) {
        if (itemMeta instanceof ArmorMeta armorMeta && this.configuration.getTrimConfiguration().enable()) {
            armorMeta.setTrim(new ArmorTrim(this.configuration.getTrimConfiguration().material(), this.configuration.getTrimConfiguration().pattern()));
        }
    }
}
