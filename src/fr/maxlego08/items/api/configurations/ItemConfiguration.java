package fr.maxlego08.items.api.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemConfiguration {

    private final Material material;
    private final int maxStackSize;
    private final String customName;
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
    private Food food;
    private boolean attributeShowInTooltip;

    public ItemConfiguration(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();

        this.material = Material.getMaterial(config.getString("material", "IRON_SWORD").toUpperCase());
        this.maxStackSize = between(config.getInt("max-stack-size", 1), 1, 99); // between 1 and 99 (inclusive)
        this.customName = config.getString("custom-name");
        this.itemName = config.getString("item-name");
        this.lore = config.getStringList("lore");
        this.maxDamage = config.getInt("max-damage", 0);
        this.damage = config.getInt("damage", 0);
        this.repairCost = config.getInt("repair-cost", 0);
        this.unbreakableEnabled = config.getBoolean("unbreakable.enabled", false);
        this.unbreakableShowInTooltip = config.getBoolean("unbreakable.show-in-tooltip", true);
        this.fireResistant = config.getBoolean("fire-resistant", false);
        this.customModelData = config.getInt("custom-model-data", 0);
        this.hideTooltip = config.getBoolean("hide-tooltip", false);
        this.hideAdditionalTooltip = config.getBoolean("hide-additional-tooltip", false);
        this.canPlaceOnBlocks = config.getStringList("can-place-on.blocks");
        this.canPlaceOnShowInTooltip = config.getBoolean("can-place-on.show-in-tooltip", true);
        this.canBreakBlocks = config.getStringList("can-break.blocks");
        this.canBreakShowInTooltip = config.getBoolean("can-break.show-in-tooltip", true);
        this.enchantmentGlint = config.getBoolean("enchantment.glint", false);
        this.enchantmentShowInTooltip = config.getBoolean("enchantment.show-in-tooltip", true);

        // Load enchantments
        this.enchantments = new ArrayList<>();
        List<Map<?, ?>> enchantmentList = config.getMapList("enchantment.enchantments");
        for (Map<?, ?> enchantmentMap : enchantmentList) {
            String type = (String) enchantmentMap.get("type");
            /*Enchantment enchantment = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(type));
            int level = (int) enchantmentMap.get("level");
            this.enchantments.add(new ItemEnchantment(enchantment, level));*/
        }

        // Load food
        if (config.contains("food")) {
            int nutrition = config.getInt("food.nutrition", 0);
            int saturation = config.getInt("food.saturation", 0);
            boolean isMeat = config.getBoolean("food.is-meat", false);
            boolean canAlwaysEat = config.getBoolean("food.can-always-eat", false);
            int eatSeconds = config.getInt("food.eat-seconds", 0);
            this.food = new Food(nutrition, saturation, isMeat, canAlwaysEat, eatSeconds, new ArrayList<>());

            List<Map<?, ?>> effectsList = config.getMapList("food.effects");
            for (Map<?, ?> effectMap : effectsList) {
                String effectType = (String) effectMap.get("type");
                double probability = (double) effectMap.get("probability");
                int amplifier = (int) effectMap.get("amplifier");
                int duration = (int) effectMap.get("duration");
                boolean showParticles = (boolean) effectMap.get("show-particles");
                boolean showIcon = (boolean) effectMap.get("show-icon");
                boolean ambient = (boolean) effectMap.get("ambient");
                this.food.addEffect(new FoodEffect(effectType, probability, amplifier, duration, showParticles, showIcon, ambient));
            }
        }
    }

    public Material getMaterial() {
        return material;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getCustomName() {
        return customName;
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
}
