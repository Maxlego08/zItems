package fr.maxlego08.items.api.configurations;

import com.destroystokyo.paper.inventory.meta.ArmorStandMeta;
import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemComponent;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.configurations.commands.CommandsConfiguration;
import fr.maxlego08.items.api.configurations.meta.*;
import fr.maxlego08.items.api.configurations.recipes.RecipeConfiguration;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.api.runes.ItemRuneConfiguration;
import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.utils.Helper;
import fr.maxlego08.items.api.utils.TrimHelper;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemConfiguration {

    private final ItemType itemType;
    private final Material material;
    private final int amount;
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
    private final List<ItemEnchantment> disableEnchantments;
    private final List<Rune> runes;
    private final List<Rune> disableRunes;
    private final boolean enchantmentShowInTooltip;
    private final List<AttributeConfiguration> attributes;
    private final boolean attributeShowInTooltip;
    private final TrimConfiguration trimConfiguration;
    private final ArmorStandConfig armorStandConfig;
    private final BlockDataMetaConfiguration blockDataMetaConfiguration;
    private final BlockStateMetaConfiguration blockStateMetaConfiguration;
    private final ToolComponentConfiguration toolComponentConfiguration;
    private final RecipeConfiguration recipeConfiguration;
    private AxolotlBucketConfiguration axolotlBucketConfiguration;
    private BannerMetaConfiguration bannerMetaConfiguration;
    private PotionMetaConfiguration potionMetaConfiguration;
    private CommandsConfiguration commandsConfiguration;
    private ItemRuneConfiguration itemRuneConfiguration;
    private Food food;
    private ItemRarity itemRarity;

    public ItemConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        this.itemType = ItemType.valueOf(configuration.getString(path + "type", "CLASSIC").toUpperCase());
        this.material = Material.getMaterial(configuration.getString(path + "material", "IRON_SWORD").toUpperCase());
        this.maxStackSize = Helper.between(configuration.getInt(path + "max-stack-size", 0), 0, 99); // between 0 and 99
        this.amount = Helper.between(configuration.getInt(path + "amount", 0), 0, 99); // between 1 and 99
        this.displayName = configuration.getString(path + "display-name");
        this.itemName = configuration.getString(path + "item-name");
        this.lore = configuration.getStringList(path + "lore");
        this.maxDamage = configuration.getInt(path + "max-damage", 0);
        this.damage = configuration.getInt(path + "damage", 0);
        this.repairCost = configuration.getInt(path + "repair-cost", 0);
        this.unbreakableEnabled = configuration.getBoolean(path + "unbreakable.enabled", false);
        this.unbreakableShowInTooltip = configuration.getBoolean(path + "unbreakable.show-in-tooltip", true);
        this.fireResistant = configuration.getBoolean(path + "fire-resistant", false);
        this.customModelData = configuration.getInt(path + "custom-model-data", 0);
        this.hideTooltip = configuration.getBoolean(path + "hide-tooltip", false);
        this.hideAdditionalTooltip = configuration.getBoolean(path + "hide-additional-tooltip", false);
        this.canPlaceOnBlocks = configuration.getStringList(path + "can-place-on.blocks");
        this.canPlaceOnShowInTooltip = configuration.getBoolean(path + "can-place-on.show-in-tooltip", true);
        this.canBreakBlocks = configuration.getStringList(path + "can-break.blocks");
        this.canBreakShowInTooltip = configuration.getBoolean(path + "can-break.show-in-tooltip", true);
        this.enchantmentGlint = configuration.getBoolean(path + "enchantment.glint", false);
        this.enchantmentShowInTooltip = configuration.getBoolean(path + "enchantment.show-in-tooltip", true);
        String rarity = configuration.getString(path + "rarity");
        if (rarity != null) {
            try {
                this.itemRarity = ItemRarity.valueOf(rarity.toUpperCase());
            } catch (Exception exception) {
                plugin.getLogger().severe("Rarity " + rarity + " was not found for the item " + fileName);
            }
        }

        // Load enchantments
        this.enchantments = new ArrayList<>();
        List<Map<?, ?>> enchantmentList = configuration.getMapList(path + "enchantment.enchantments");
        Enchantments enchantments = plugin.getEnchantments();
        for (Map<?, ?> enchantmentMap : enchantmentList) {
            String enchantmentAsString = (String) enchantmentMap.get("enchantment");
            int level = ((Number) enchantmentMap.get("level")).intValue();
            var optional = enchantments.getEnchantments(enchantmentAsString.toLowerCase());
            if (optional.isPresent()) {
                var enchantment = optional.get().enchantment();
                this.enchantments.add(new ItemEnchantment(enchantment, level));
            } else {
                plugin.getLogger().severe("Enchantment " + enchantmentAsString + " was not found for the item " + fileName);
            }
        }

        this.disableEnchantments = new ArrayList<>();
        List<Map<?, ?>> enchantmentDisableList = configuration.getMapList(path + "enchantment.disable-enchantments");
        Enchantments enchantmentsDisable = plugin.getEnchantments();
        for (Map<?, ?> enchantmentMap : enchantmentDisableList) {
            String enchantmentAsString = (String) enchantmentMap.get("enchantment");
            var optional = enchantmentsDisable.getEnchantments(enchantmentAsString.toLowerCase());
            Object levels = enchantmentMap.get("levels");
            if (levels instanceof List) {
                List<Integer> levelsList = (List<Integer>) levels;
                for (Integer level : levelsList) {
                    if (optional.isPresent()) {
                        var enchantment = optional.get().enchantment();
                        this.disableEnchantments.add(new ItemEnchantment(enchantment, level));
                    } else {
                        plugin.getLogger().severe("Enchantment " + enchantmentAsString + " was not found for the item " + fileName);
                    }
                }
            } else if (levels instanceof String string) {
                if(string.equalsIgnoreCase("all")) {
                    if (optional.isPresent()) {
                        var enchantment = optional.get().enchantment();
                        this.disableEnchantments.add(new ItemEnchantment(enchantment, -1));
                    } else {
                        plugin.getLogger().severe("Enchantment " + enchantmentAsString + " was not found for the item " + fileName);
                    }
                }
            }
        }

        List<String> runesList = configuration.getStringList(path + "rune.runes");
        this.runes = runesList.stream()
                .map(runeName -> plugin.getRuneManager().getRune(runeName)
                        .orElseThrow(() -> new IllegalArgumentException("Rune " + runeName + " was not found for the item " + fileName)))
                .collect(Collectors.toList());

        List<String> runesDisableList = configuration.getStringList(path + "rune.disable-runes");
        this.disableRunes = runesDisableList.stream()
                .map(runeName -> plugin.getRuneManager().getRune(runeName)
                        .orElseThrow(() -> new IllegalArgumentException("Rune " + runeName + " was not found for the item " + fileName)))
                .collect(Collectors.toList());

        // Load food
        if (configuration.contains(path + "food")) {
            boolean enable = configuration.getBoolean(path + "food.enable", false);
            int nutrition = configuration.getInt(path + "food.nutrition", 0);
            int saturation = configuration.getInt(path + "food.saturation", 0);
            boolean canAlwaysEat = configuration.getBoolean(path + "food.can-always-eat", false);
            int eatSeconds = configuration.getInt(path + "food.eat-seconds", 0);
            String usingConvertsTo = configuration.getString(path + "food.using-converts-to", null);
            this.food = new Food(enable, nutrition, saturation, canAlwaysEat, eatSeconds, new ArrayList<>(), usingConvertsTo);

            List<Map<?, ?>> effectsList = configuration.getMapList(path + "food.effects");
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

        this.attributeShowInTooltip = configuration.getBoolean(path + "attribute.show-in-tooltip", true);
        this.attributes = configuration.getMapList(path + "attribute.attributes").stream().map(map -> {
            Attribute attribute = Attribute.valueOf(((String) map.get("attribute")).toUpperCase());
            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(((String) map.get("operation")).toUpperCase());
            double amount = ((Number) map.get("amount")).doubleValue();
            EquipmentSlotGroup slot = map.containsKey("slot") ? EquipmentSlotGroup.getByName((String) map.get("slot")) : EquipmentSlotGroup.ANY;

            return new AttributeConfiguration(attribute, operation, amount, slot);
        }).toList();

        boolean enableTrim = configuration.getBoolean(path + "trim.enable", false);
        if (enableTrim) {
            TrimHelper trimHelper = plugin.getTrimHelper();
            TrimPattern trimPattern = trimHelper.getTrimPatterns().get(configuration.getString(path + "trim.pattern", "").toLowerCase());
            if (trimPattern == null) {
                enableTrim = false;
                plugin.getLogger().severe("Trim pattern " + configuration.getString(path + "trim.pattern", "") + " was not found for item " + fileName);
            }
            TrimMaterial trimMaterial = trimHelper.getTrimMaterials().get(configuration.getString(path + "trim.material", "").toLowerCase());
            if (trimMaterial == null) {
                enableTrim = false;
                plugin.getLogger().severe("Trim material " + configuration.getString(path + "trim.material", "") + " was not found for item " + fileName);
            }
            this.trimConfiguration = new TrimConfiguration(enableTrim, trimMaterial, trimPattern);

        } else this.trimConfiguration = new TrimConfiguration(false, null, null);

        this.armorStandConfig = new ArmorStandConfig(configuration.getBoolean(path + "armor-stand.enable"), configuration.getBoolean(path + "armor-stand.invisible"), configuration.getBoolean(path + "armor-stand.no_base_plate"), configuration.getBoolean(path + "armor-stand.show_arms"), configuration.getBoolean(path + "armor-stand.small"), configuration.getBoolean(path + "armor-stand.marker"));

        this.loadAxolotl(plugin, configuration, fileName, path);
        this.loadBanner(plugin, configuration, fileName, path);
        this.loadPotion(plugin, configuration, fileName, path);
        this.blockDataMetaConfiguration = BlockDataMetaConfiguration.loadBlockDataMeta(plugin, configuration, fileName, path);
        this.blockStateMetaConfiguration = BlockStateMetaConfiguration.loadBlockStateMeta(plugin, configuration, fileName, path);
        this.toolComponentConfiguration = ToolComponentConfiguration.loadToolComponent(plugin, configuration, fileName, path);
        this.recipeConfiguration = RecipeConfiguration.loadRecipe(plugin, configuration, fileName, path);
        this.commandsConfiguration = CommandsConfiguration.loadCommandsConfiguration(plugin, configuration, fileName, path);

        if (this.itemType == ItemType.RUNE) {
            this.itemRuneConfiguration = ItemRuneConfiguration.loadItemRuneConfiguration(plugin, configuration, fileName, path);
        }

    }

    private void loadPotion(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        boolean enablePotion = configuration.getBoolean(path + "potion-meta.enable", false);

        if (enablePotion) {
            try {

                Color potionColor = Helper.getColor(configuration, path + "potion-meta.color", null);
                PotionType basePotionType = null;
                String value = configuration.getString(path + "potion-meta.base-potion-type");
                if (value != null) {
                    basePotionType = PotionType.valueOf(value.toUpperCase());
                }
                List<CustomPotionEffect> customEffects = new ArrayList<>();
                List<Map<?, ?>> effectsList = configuration.getMapList(path + "potion-meta.custom-effect");

                for (Map<?, ?> map : effectsList) {
                    boolean overwrite = map.containsKey("overwrite") ? (Boolean) map.get("overwrite") : false;
                    PotionEffectType type = map.containsKey("type") ? PotionMetaConfiguration.potionEffectTypeMap.get(((String) map.get("type")).toLowerCase()) : PotionEffectType.STRENGTH;
                    int duration = map.containsKey("duration") ? ((Number) map.get("duration")).intValue() : 100;
                    int amplifier = map.containsKey("amplifier") ? ((Number) map.get("amplifier")).intValue() : 0;
                    boolean ambient = map.containsKey("ambient") ? (Boolean) map.get("ambient") : true;
                    boolean particles = map.containsKey("particles") ? (Boolean) map.get("particles") : true;
                    boolean icon = map.containsKey("icon") ? (Boolean) map.get("icon") : true;

                    CustomPotionEffect effect = new CustomPotionEffect(overwrite, type, duration, amplifier, ambient, particles, icon);
                    customEffects.add(effect);
                }

                this.potionMetaConfiguration = new PotionMetaConfiguration(true, potionColor, customEffects, basePotionType);

            } catch (Exception exception) {
                plugin.getLogger().severe("Impossible to load the potion meta for item " + fileName);
                exception.printStackTrace();
                this.potionMetaConfiguration = new PotionMetaConfiguration(false, null, null, null);
            }
        } else {
            this.potionMetaConfiguration = new PotionMetaConfiguration(false, null, null, null);
        }
    }

    private void loadAxolotl(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableAxolotl = configuration.getBoolean(path + "axolotl-bucket.enable", false);
        if (enableAxolotl) {
            try {
                this.axolotlBucketConfiguration = new AxolotlBucketConfiguration(true, Axolotl.Variant.valueOf(configuration.getString(path + "axolotl-bucket.variant")));
            } catch (Exception ignored) {
                plugin.getLogger().severe("Axolotl variant " + configuration.getString(path + "axolotl-bucket.variant", "") + " was not found for item " + fileName);
                this.axolotlBucketConfiguration = new AxolotlBucketConfiguration(false, null);
            }
        } else {
            this.axolotlBucketConfiguration = new AxolotlBucketConfiguration(false, null);
        }
    }

    private void loadBanner(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBannerMeta = configuration.getBoolean(path + "banner-meta.enable", false);
        List<Pattern> patterns = new ArrayList<>();

        if (enableBannerMeta) {
            List<?> patternsList = configuration.getList(path + "banner-meta.patterns");
            if (patternsList != null) {
                for (Object obj : patternsList) {
                    if (obj instanceof ConfigurationSection patternSection) {
                        try {
                            DyeColor color = DyeColor.valueOf(patternSection.getString("color"));
                            PatternType patternType = PatternType.valueOf(patternSection.getString("pattern"));
                            patterns.add(new Pattern(color, patternType));
                        } catch (Exception e) {
                            plugin.getLogger().severe("Invalid pattern configuration in " + fileName + ": " + patternSection);
                        }
                    }
                }
            }
            this.bannerMetaConfiguration = new BannerMetaConfiguration(true, patterns);
        } else {
            this.bannerMetaConfiguration = new BannerMetaConfiguration(false, null);
        }
    }

    public BlockDataMetaConfiguration getBlockDataMetaConfiguration() {
        return blockDataMetaConfiguration;
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

    public List<ItemEnchantment> getDisableEnchantments() {
        return disableEnchantments;
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

    public ItemType getItemType() {
        return itemType;
    }

    public void enchant(ItemMeta itemMeta) {

        this.enchantments.forEach(itemEnchantment -> {

            var enchantment = itemEnchantment.enchantment();
            var level = itemEnchantment.level();

            if (itemMeta instanceof EnchantmentStorageMeta enchantmentStorageMeta) {
                if (level == 0) enchantmentStorageMeta.removeStoredEnchant(enchantment);
                else enchantmentStorageMeta.addStoredEnchant(enchantment, level, true);
            } else {
                if (level == 0) itemMeta.removeEnchant(enchantment);
                else itemMeta.addEnchant(enchantment, level, true);
            }
        });
    }

    public int getAmount() {
        return amount;
    }

    public TrimConfiguration getTrimConfiguration() {
        return trimConfiguration;
    }

    public List<AttributeConfiguration> getAttributes() {
        return attributes;
    }

    public ArmorStandConfig getArmorStandConfig() {
        return armorStandConfig;
    }

    public void applyArmorStand(ItemMeta itemMeta) {
        if (itemMeta instanceof ArmorStandMeta armorStandMeta && this.armorStandConfig.enable()) {
            armorStandMeta.setInvisible(this.armorStandConfig.invisible());
            armorStandMeta.setNoBasePlate(this.armorStandConfig.noBasePlate());
            armorStandMeta.setShowArms(this.armorStandConfig.showArms());
            armorStandMeta.setSmall(this.armorStandConfig.small());
            armorStandMeta.setMarker(this.armorStandConfig.marker());
        }
    }

    public void applyTrim(ItemMeta itemMeta) {
        if (itemMeta instanceof ArmorMeta armorMeta && this.trimConfiguration.enable()) {
            armorMeta.setTrim(new ArmorTrim(this.trimConfiguration.material(), this.trimConfiguration.pattern()));
        }
    }

    public void applyAxolotlBucket(ItemMeta itemMeta) {
        if (itemMeta instanceof AxolotlBucketMeta axolotlBucketMeta && this.axolotlBucketConfiguration.enable()) {
            axolotlBucketMeta.setVariant(this.axolotlBucketConfiguration.variant());
        }
    }

    public void applyBanner(ItemMeta itemMeta) {
        if (itemMeta instanceof BannerMeta bannerMeta && this.bannerMetaConfiguration.enable()) {
            bannerMeta.setPatterns(this.bannerMetaConfiguration.patterns());
        }
    }

    public void applyBlockDataMeta(ItemMeta itemMeta) {
        if (itemMeta instanceof BlockDataMeta blockDataMeta && this.blockDataMetaConfiguration.enable()) {
            blockDataMeta.setBlockData(this.blockDataMetaConfiguration.blockData());
        }
    }

    public void applyToolComponent(ItemMeta itemMeta) {

        if (!this.toolComponentConfiguration.enable()) return;

        ToolComponent toolComponent = itemMeta.getTool();
        this.toolComponentConfiguration.apply(toolComponent);

        itemMeta.setTool(toolComponent);
    }

    public void applyPotionMeta(ItemMeta itemMeta) {
        if (itemMeta instanceof PotionMeta potionMeta && this.potionMetaConfiguration.enable()) {

            if (this.potionMetaConfiguration.color() != null) {
                potionMeta.setColor(this.potionMetaConfiguration.color());
            }

            if (this.potionMetaConfiguration.basePotionType() != null) {
                potionMeta.setBasePotionType(this.potionMetaConfiguration.basePotionType());
            }

            for (CustomPotionEffect customPotionEffect : this.potionMetaConfiguration.customPotionEffects()) {
                potionMeta.addCustomEffect(new PotionEffect(customPotionEffect.type(), customPotionEffect.duration(), customPotionEffect.amplifier(), customPotionEffect.ambient(), customPotionEffect.particles(), customPotionEffect.icon()), customPotionEffect.overwrite());
            }
        }
    }

    public void applyBlockState(ItemMeta itemMeta, Player player, ItemComponent itemComponent) {
        if (this.blockStateMetaConfiguration != null && blockStateMetaConfiguration.enable() && itemMeta instanceof BlockStateMeta blockStateMeta) {
            this.blockStateMetaConfiguration.apply(blockStateMeta, player, itemComponent);
        }
    }

    public void createRecipe(Item item, ItemsPlugin plugin) {
        this.recipeConfiguration.apply(item, plugin);
    }

    public void deleteRecipe(Item item, ItemsPlugin plugin) {
        this.recipeConfiguration.deleteRecipe(item, plugin);
    }

    public ItemRarity getItemRarity() {
        return itemRarity;
    }

    public RecipeConfiguration getRecipeConfiguration() {
        return recipeConfiguration;
    }

    public CommandsConfiguration getCommandsConfiguration() {
        return commandsConfiguration;
    }

    public List<Rune> getDisableRunes() {
        return disableRunes;
    }

    public List<Rune> getRunes() {
        return runes;
    }

    public ItemRuneConfiguration getItemRuneConfiguration() {
        if(this.itemType != ItemType.RUNE || this.itemRuneConfiguration == null) {
            throw new IllegalArgumentException("Item is not a rune item or the rune configuration is null");
        }
        return itemRuneConfiguration;
    }
}
