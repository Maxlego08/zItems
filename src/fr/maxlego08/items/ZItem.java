package fr.maxlego08.items;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.configurations.Food;
import fr.maxlego08.items.api.configurations.ItemConfiguration;
import fr.maxlego08.items.api.configurations.RecipeConfiguration;
import fr.maxlego08.items.zcore.utils.ZUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;
import java.util.UUID;

public class ZItem extends ZUtils implements Item {

    private final ItemsPlugin plugin;
    private final String name;
    private final ItemConfiguration configuration;

    public ZItem(ItemsPlugin plugin, String name, ItemConfiguration configuration) {
        this.plugin = plugin;
        this.name = name;
        this.configuration = configuration;
        this.configuration.createRecipe(plugin, this, name);
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

        int currentAmount = amount == 0 ? this.configuration.getAmount() : amount;
        currentAmount = currentAmount == 0 ? 1 : currentAmount;
        itemStack.setAmount(currentAmount);

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta != null) {

            PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
            persistentDataContainer.set(ITEM_KEY, PersistentDataType.STRING, this.name);

            if (this.configuration.getMaxStackSize() > 0) {
                itemMeta.setMaxStackSize(this.configuration.getMaxStackSize());
            }

            this.applyNames(itemMeta, player);
            this.applyFood(itemMeta, player);
            this.configuration.applyTrim(itemMeta);
            this.configuration.applyArmorStand(itemMeta);
            this.configuration.applyAxolotlBucket(itemMeta);
            this.configuration.applyBanner(itemMeta);
            this.configuration.applyPotionMeta(itemMeta);
            this.configuration.applyBlockDataMeta(itemMeta);
            this.configuration.applyBlockState(itemMeta, player, this.plugin.getItemComponent());
            this.configuration.applyToolComponent(itemMeta);

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

            this.configuration.enchant(itemMeta);
            if (!this.configuration.isEnchantmentShowInTooltip()) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            itemMeta.setEnchantmentGlintOverride(this.configuration.isEnchantmentGlint());

            this.configuration.getAttributes().forEach(attributeConfiguration -> itemMeta.addAttributeModifier(attributeConfiguration.attribute(), new AttributeModifier(NamespacedKey.fromString(UUID.randomUUID().toString()), attributeConfiguration.amount(), attributeConfiguration.operation(), attributeConfiguration.slot())));

            if (!this.configuration.isAttributeShowInTooltip()) {
                itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            }

            if (this.configuration.getItemRarity() != null) {
                itemMeta.setRarity(this.configuration.getItemRarity());
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
            itemComponent.setDisplayName(itemMeta, papi(this.configuration.getDisplayName(), player));
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
}
