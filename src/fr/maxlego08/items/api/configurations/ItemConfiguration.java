package fr.maxlego08.items.api.configurations;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.trim.TrimHelper;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemConfiguration {

    private final ItemType itemType;
    private final Material material;
    private final int maxStackSize;
    private final String displayName;
    private final String itemName;
    private final List<String> lore;
    private final int maxDamage;
    private final int damage;
    private final int repairCost;
    private final boolean unbreakableEnabled;
    private final boolean unbreakableShowInTooltip;
    private final boolean fireResistant;
    private final int customModelData;
    private final boolean hideTooltip;
    private final boolean hideAdditionalTooltip;
    private final List<String> canPlaceOnBlocks;
    private final boolean canPlaceOnShowInTooltip;
    private final List<String> canBreakBlocks;
    private final boolean canBreakShowInTooltip;
    private final boolean enchantmentGlint;
    private final List<ItemEnchantment> enchantments;
    private final boolean enchantmentShowInTooltip;
    private final List<AttributeConfiguration> attributes;
    private final boolean attributeShowInTooltip;
    private final TrimConfiguration trimConfiguration;
    private Food food;

    public ItemConfiguration(ItemsPlugin plugin, YamlConfiguration configuration, String fileName) {

        this.itemType = ItemType.valueOf(configuration.getString("type", "CLASSIC").toUpperCase());
        this.material = Material.getMaterial(configuration.getString("material", "IRON_SWORD").toUpperCase());
        this.maxStackSize = between(configuration.getInt("max-stack-size", 0), 0, 99); // between 1 and 99 (inclusive)
        this.displayName = configuration.getString("display-name");
        this.itemName = configuration.getString("item-name");
        this.lore = configuration.getStringList("lore");
        this.maxDamage = configuration.getInt("max-damage", 0);
        this.damage = configuration.getInt("damage", 0);
        this.repairCost = configuration.getInt("repair-cost", 0);
        this.unbreakableEnabled = configuration.getBoolean("unbreakable.enabled", false);
        this.unbreakableShowInTooltip = configuration.getBoolean("unbreakable.show-in-tooltip", true);
        this.fireResistant = configuration.getBoolean("fire-resistant", false);
        this.customModelData = configuration.getInt("custom-model-data", 0);
        this.hideTooltip = configuration.getBoolean("hide-tooltip", false);
        this.hideAdditionalTooltip = configuration.getBoolean("hide-additional-tooltip", false);
        this.canPlaceOnBlocks = configuration.getStringList("can-place-on.blocks");
        this.canPlaceOnShowInTooltip = configuration.getBoolean("can-place-on.show-in-tooltip", true);
        this.canBreakBlocks = configuration.getStringList("can-break.blocks");
        this.canBreakShowInTooltip = configuration.getBoolean("can-break.show-in-tooltip", true);
        this.enchantmentGlint = configuration.getBoolean("enchantment.glint", false);
        this.enchantmentShowInTooltip = configuration.getBoolean("enchantment.show-in-tooltip", true);

        // Load enchantments
        this.enchantments = new ArrayList<>();
        List<Map<?, ?>> enchantmentList = configuration.getMapList("enchantment.enchantments");
        Enchantments enchantments = plugin.getEnchantments();
        for (Map<?, ?> enchantmentMap : enchantmentList) {
            String enchantmentAsString = (String) enchantmentMap.get("enchantment");
            int level = ((Number) enchantmentMap.get("level")).intValue();
            var optional = enchantments.getEnchantments(enchantmentAsString.toLowerCase());
            if (optional.isPresent()) {
                var enchantment = optional.get().enchantment();
                this.enchantments.add(new ItemEnchantment(enchantment, level));
            } else {
                plugin.getLogger().severe("Enchantment " + enchantmentAsString + " was not found !");
            }
        }

        // Load food
        if (configuration.contains("food")) {
            boolean enable = configuration.getBoolean("food.enable", false);
            int nutrition = configuration.getInt("food.nutrition", 0);
            int saturation = configuration.getInt("food.saturation", 0);
            boolean canAlwaysEat = configuration.getBoolean("food.can-always-eat", false);
            int eatSeconds = configuration.getInt("food.eat-seconds", 0);
            String usingConvertsTo = configuration.getString("food.using-converts-to", null);
            this.food = new Food(enable, nutrition, saturation, canAlwaysEat, eatSeconds, new ArrayList<>(), usingConvertsTo);

            List<Map<?, ?>> effectsList = configuration.getMapList("food.effects");
            for (Map<?, ?> effectMap : effectsList) {
                String effectType = (String) effectMap.get("type");
                double probability = ((Number) effectMap.get("probability")).doubleValue();
                int amplifier = ((Number) effectMap.get("amplifier")).intValue();
                int duration = ((Number) effectMap.get("duration")).intValue();
                boolean showParticles = (boolean) effectMap.get("show-particles");
                boolean showIcon = (boolean) effectMap.get("show-icon");
                boolean ambient = (boolean) effectMap.get("ambient");
                this.food.addEffect(new FoodEffect(effectType, (float) probability, amplifier, duration, showParticles, showIcon, ambient));
            }
        }

        this.attributeShowInTooltip = configuration.getBoolean("attribute.show-in-tooltip", true);
        this.attributes = configuration.getMapList("attribute.attributes").stream().map(map -> {
            Attribute attribute = Attribute.valueOf(((String) map.get("attribute")).toUpperCase());
            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(((String) map.get("operation")).toUpperCase());
            double amount = ((Number) map.get("amount")).doubleValue();
            EquipmentSlotGroup slot = map.containsKey("slot") ? EquipmentSlotGroup.getByName((String) map.get("slot")) : EquipmentSlotGroup.ANY;

            return new AttributeConfiguration(attribute, operation, amount, slot);
        }).toList();

        boolean enableTrim = configuration.getBoolean("trim.enable", false);
        if (enableTrim) {
            TrimHelper trimHelper = plugin.getTrimHelper();
            TrimPattern trimPattern = trimHelper.getTrimPatterns().get(configuration.getString("trim.pattern", "").toLowerCase());
            if (trimPattern == null) {
                enableTrim = false;
                plugin.getLogger().severe("Trim pattern " + configuration.getString("trim.pattern", "") + " was not found for item " + fileName);
            }
            TrimMaterial trimMaterial = trimHelper.getTrimMaterials().get(configuration.getString("trim.material", "").toLowerCase());
            if (trimMaterial == null) {
                enableTrim = false;
                plugin.getLogger().severe("Trim material " + configuration.getString("trim.material", "") + " was not found for item " + fileName);
            }
            this.trimConfiguration = new TrimConfiguration(enableTrim, trimMaterial, trimPattern);

        } else this.trimConfiguration = new TrimConfiguration(false, null, null);

    }

    public Material getMaterial() {
        return material;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getItemName() {
        return itemName;
    }

    public List<String> getLore() {
        return lore;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getDamage() {
        return damage;
    }

    public int getRepairCost() {
        return repairCost;
    }

    public boolean isUnbreakableEnabled() {
        return unbreakableEnabled;
    }

    public boolean isUnbreakableShowInTooltip() {
        return unbreakableShowInTooltip;
    }

    public boolean isFireResistant() {
        return fireResistant;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public boolean isHideTooltip() {
        return hideTooltip;
    }

    public boolean isHideAdditionalTooltip() {
        return hideAdditionalTooltip;
    }

    public List<String> getCanPlaceOnBlocks() {
        return canPlaceOnBlocks;
    }

    public boolean isCanPlaceOnShowInTooltip() {
        return canPlaceOnShowInTooltip;
    }

    public List<String> getCanBreakBlocks() {
        return canBreakBlocks;
    }

    public boolean isCanBreakShowInTooltip() {
        return canBreakShowInTooltip;
    }

    public boolean isEnchantmentGlint() {
        return enchantmentGlint;
    }

    public List<ItemEnchantment> getEnchantments() {
        return enchantments;
    }

    public boolean isEnchantmentShowInTooltip() {
        return enchantmentShowInTooltip;
    }

    public Food getFood() {
        return food;
    }

    public boolean isAttributeShowInTooltip() {
        return attributeShowInTooltip;
    }

    private int between(int value, int min, int max) {
        return value > max ? max : Math.max(value, min);
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void enchant(ItemStack itemStack) {

        this.enchantments.forEach(itemEnchantment -> {

            var enchantment = itemEnchantment.enchantment();
            var level = itemEnchantment.level();

            if (itemStack.getType() == Material.ENCHANTED_BOOK) {
                var enchantmentStorageMeta = (EnchantmentStorageMeta) itemStack.getItemMeta();
                if (level == 0) enchantmentStorageMeta.removeStoredEnchant(enchantment);
                else enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
                itemStack.setItemMeta(enchantmentStorageMeta);
            } else {
                if (level == 0) itemStack.removeEnchantment(enchantment);
                else itemStack.addUnsafeEnchantment(enchantment, level);
            }
        });
    }

    public TrimConfiguration getTrimConfiguration() {
        return trimConfiguration;
    }

    public List<AttributeConfiguration> getAttributes() {
        return attributes;
    }
}
