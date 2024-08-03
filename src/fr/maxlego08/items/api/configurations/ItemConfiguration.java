package fr.maxlego08.items.api.configurations;

import com.destroystokyo.paper.inventory.meta.ArmorStandMeta;
import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemType;
import fr.maxlego08.items.api.enchantments.Enchantments;
import fr.maxlego08.items.trim.TrimHelper;
import org.bukkit.Axis;
import org.bukkit.DyeColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.AnaloguePowerable;
import org.bukkit.block.data.Attachable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Brushable;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Hangable;
import org.bukkit.block.data.Hatchable;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.Lightable;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.Orientable;
import org.bukkit.block.data.Powerable;
import org.bukkit.block.data.Rail;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.Snowable;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.Bamboo;
import org.bukkit.block.data.type.Beehive;
import org.bukkit.block.data.type.BrewingStand;
import org.bukkit.block.data.type.BubbleColumn;
import org.bukkit.block.data.type.Cake;
import org.bukkit.block.data.type.Campfire;
import org.bukkit.block.data.type.Candle;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.block.data.type.Comparator;
import org.bukkit.block.data.type.Crafter;
import org.bukkit.block.data.type.DaylightDetector;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.block.data.type.Gate;
import org.bukkit.block.data.type.Jigsaw;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.block.data.type.Piston;
import org.bukkit.block.data.type.PistonHead;
import org.bukkit.block.data.type.PointedDripstone;
import org.bukkit.block.data.type.Repeater;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.block.data.type.Scaffolding;
import org.bukkit.block.data.type.SculkCatalyst;
import org.bukkit.block.data.type.SculkSensor;
import org.bukkit.block.data.type.SculkShrieker;
import org.bukkit.block.data.type.SeaPickle;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Snow;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TNT;
import org.bukkit.block.data.type.TechnicalPiston;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.TrialSpawner;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.block.data.type.TripwireHook;
import org.bukkit.block.data.type.TurtleEgg;
import org.bukkit.block.data.type.Vault;
import org.bukkit.block.data.type.Wall;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private final ArmorStandConfig armorStandConfig;
    private AxolotlBucketConfiguration axolotlBucketConfiguration;
    private BannerMetaConfiguration bannerMetaConfiguration;
    private BlockDataMetaConfiguration blockDataMetaConfiguration;
    private Food food;

    public ItemConfiguration(ItemsPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        this.itemType = ItemType.valueOf(configuration.getString(path + "type", "CLASSIC").toUpperCase());
        this.material = Material.getMaterial(configuration.getString(path + "material", "IRON_SWORD").toUpperCase());
        this.maxStackSize = between(configuration.getInt(path + "max-stack-size", 0), 0, 99); // between 1 and 99 (inclusive)
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
                plugin.getLogger().severe("Enchantment " + enchantmentAsString + " was not found !");
            }
        }

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
        this.loadBlockDataMeta(plugin, configuration, fileName, path);
    }

    private void loadAxolotl(ItemsPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
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

    private void loadBanner(ItemsPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
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

    private void loadBlockDataMeta(Plugin plugin, YamlConfiguration configuration, String fileName, String path) {
        boolean enableBlockDataMeta = configuration.getBoolean(path + "block-data-meta.enable", false);
        BlockData blockData = null;

        if (enableBlockDataMeta) {
            try {
                Material material = Material.valueOf(configuration.getString(path + "block-data-meta.material"));
                blockData = plugin.getServer().createBlockData(material);

                if (blockData instanceof Directional directional) {
                    String face = configuration.getString(path + "block-data-meta.block-face");
                    if (face != null) directional.setFacing(BlockFace.valueOf(face.toUpperCase()));
                }

                if (blockData instanceof Waterlogged waterlogged) {
                    waterlogged.setWaterlogged(configuration.getBoolean(path + "block-data-meta.waterlogged"));
                }

                if (blockData instanceof Attachable attachable) {
                    attachable.setAttached(configuration.getBoolean(path + "block-data-meta.attached"));
                }

                if (blockData instanceof Openable openable) {
                    openable.setOpen(configuration.getBoolean(path + "block-data-meta.open"));
                }

                if (blockData instanceof Ageable ageable) {
                    if (configuration.getString(path + "block-data-meta.age", "").equalsIgnoreCase("max")) {
                        ageable.setAge(ageable.getMaximumAge());
                    } else ageable.setAge(configuration.getInt(path + "block-data-meta.age", 0));
                }

                if (blockData instanceof Sapling sapling) {
                    if (configuration.getString(path + "block-data-meta.stage", "").equalsIgnoreCase("max")) {
                        sapling.setStage(sapling.getMaximumStage());
                    } else sapling.setStage(configuration.getInt(path + "block-data-meta.stage", 0));
                }

                if (blockData instanceof AnaloguePowerable analoguePowerable) {
                    if (configuration.getString(path + "block-data-meta.power", "").equalsIgnoreCase("max")) {
                        analoguePowerable.setPower(analoguePowerable.getMaximumPower());
                    } else analoguePowerable.setPower(configuration.getInt(path + "block-data-meta.power", 0));
                }

                if (blockData instanceof Bamboo bamboo) {
                    String leaves = configuration.getString(path + "block-data-meta.bamboo-leaves");
                    if (leaves != null) bamboo.setLeaves(Bamboo.Leaves.valueOf(leaves.toUpperCase()));
                }

                if (blockData instanceof Orientable orientable) {
                    String axisAsString = configuration.getString(path + "block-data-meta.axis");
                    if (axisAsString != null) {
                        Axis axis = Axis.valueOf(axisAsString.toUpperCase());
                        if (orientable.getAxes().contains(axis)) {
                            orientable.setAxis(axis);
                        }
                    }
                }

                if (blockData instanceof Beehive beehive) {
                    int honeyLevel = configuration.getInt(path + "block-data-meta.honey-level", 0);
                    if (honeyLevel < 0 || honeyLevel > beehive.getMaximumHoneyLevel()) {
                        throw new IllegalArgumentException("Honey level must be between 0 and " + beehive.getMaximumHoneyLevel());
                    }
                    beehive.setHoneyLevel(honeyLevel);
                }

                if (blockData instanceof Powerable powerable) {
                    powerable.setPowered(configuration.getBoolean(path + "block-data-meta.powered", false));
                }

                if (blockData instanceof Bisected bisected) {
                    String halfAsString = configuration.getString(path + "block-data-meta.half");
                    if (halfAsString != null) {
                        bisected.setHalf(Bisected.Half.valueOf(halfAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof BrewingStand brewingStand) {
                    List<Map<?, ?>> bottles = configuration.getMapList(path + "block-data-meta.bottles");
                    for (int i = 0; i < bottles.size(); i++) {
                        Map<?, ?> bottle = bottles.get(i);
                        brewingStand.setBottle(i, (boolean) bottle.get(i));
                    }
                }

                if (blockData instanceof Brushable brushable) {
                    int dusted = configuration.getInt(path + "block-data-meta.dusted", 0);
                    if (dusted < 0 || dusted > brushable.getMaximumDusted()) {
                        throw new IllegalArgumentException("Dusted must be between 0 and " + brushable.getMaximumDusted());
                    }
                    brushable.setDusted(dusted);
                }

                if (blockData instanceof BubbleColumn bubbleColumn) {
                    bubbleColumn.setDrag(configuration.getBoolean(path + "block-data-meta.drag", false));
                }

                if (blockData instanceof Cake cake) {
                    int bites = configuration.getInt(path + "block-data-meta.bites", 0);
                    if (bites < 0 || bites > cake.getMaximumBites()) {
                        throw new IllegalArgumentException("Bites must be between 0 and " + cake.getMaximumBites());
                    }
                    cake.setBites(bites);
                }

                if (blockData instanceof Campfire campfire) {
                    campfire.setSignalFire(configuration.getBoolean(path + "block-data-meta.signal-fire", false));
                }

                if (blockData instanceof Candle candle) {
                    int candles = configuration.getInt(path + "block-data-meta.candles", 1);
                    if (candles < 1 || candles > candle.getMaximumCandles()) {
                        throw new IllegalArgumentException("Candles must be between 1 and " + candle.getMaximumCandles());
                    }
                    candle.setCandles(candles);
                }

                if (blockData instanceof CaveVinesPlant caveVinesPlant) {
                    caveVinesPlant.setBerries(configuration.getBoolean(path + "block-data-meta.berries", false));
                }

                if (blockData instanceof Chest chest) {
                    String typeAsString = configuration.getString(path + "block-data-meta.chest-type");
                    if (typeAsString != null) {
                        chest.setType(Chest.Type.valueOf(typeAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof CommandBlock commandBlock) {
                    commandBlock.setConditional(configuration.getBoolean(path + "block-data-meta.conditional", false));
                }

                if (blockData instanceof Comparator comparator) {
                    String modeAsString = configuration.getString(path + "block-data-meta.comparator-mode");
                    if (modeAsString != null) {
                        comparator.setMode(Comparator.Mode.valueOf(modeAsString.toUpperCase()));
                    }
                }

                if (blockData instanceof Crafter crafter) {
                    boolean crafting = configuration.getBoolean(path + "block-data-meta.crafter-crafting", false);
                    boolean triggered = configuration.getBoolean(path + "block-data-meta.crafter-triggered", false);
                    String orientationAsString = configuration.getString(path + "block-data-meta.crafter-orientation");
                    if (orientationAsString != null) {
                        crafter.setOrientation(Crafter.Orientation.valueOf(orientationAsString.toUpperCase()));
                    }
                    crafter.setCrafting(crafting);
                    crafter.setTriggered(triggered);
                }

                if (blockData instanceof DaylightDetector daylightDetector) {
                    daylightDetector.setInverted(configuration.getBoolean(path + "block-data-meta.inverted", false));
                }

                if (blockData instanceof Door door) {
                    String hinge = configuration.getString(path + "block-data-meta.hinge");
                    if (hinge != null) {
                        door.setHinge(Door.Hinge.valueOf(hinge));
                    }
                }

                if (blockData instanceof EndPortalFrame endPortalFrame) {
                    endPortalFrame.setEye(configuration.getBoolean(path + "block-data-meta.eye", false));
                }

                if (blockData instanceof Farmland farmland) {
                    if (configuration.getString(path + "block-data-meta.moisture", "").equalsIgnoreCase("max")) {
                        farmland.setMoisture(farmland.getMaximumMoisture());
                    } else farmland.setMoisture(configuration.getInt(path + "block-data-meta.moisture", 0));
                }

                if (blockData instanceof Gate gate) {
                    gate.setInWall(configuration.getBoolean(path + "block-data-meta.in_wall", false));
                }

                if (blockData instanceof Hangable hangable) {
                    hangable.setHanging(configuration.getBoolean(path + "block-data-meta.hanging", false));
                }

                if (blockData instanceof Hatchable hatchable) {
                    if (configuration.getString(path + "block-data-meta.hatch", "").equalsIgnoreCase("max")) {
                        hatchable.setHatch(hatchable.getMaximumHatch());
                    } else hatchable.setHatch(configuration.getInt(path + "block-data-meta.hatch", 0));
                }

                if (blockData instanceof Jigsaw jigsaw) {
                    String orientationAsString = configuration.getString(path + "block-data-meta.jigsaw-orientation");
                    if (orientationAsString != null) {
                        jigsaw.setOrientation(Jigsaw.Orientation.valueOf(orientationAsString));
                    }
                }

                if (blockData instanceof Leaves leaves) {
                    leaves.setPersistent(configuration.getBoolean(path + "block-data-meta.persistent", false));
                    leaves.setDistance(configuration.getInt(path + "block-data-meta.distance", 1));
                }

                if (blockData instanceof Levelled levelled) {
                    if (configuration.getString(path + "block-data-meta.level", "").equalsIgnoreCase("max")) {
                        levelled.setLevel(levelled.getMaximumLevel());
                    } else levelled.setLevel(configuration.getInt(path + "block-data-meta.level", 0));
                }

                if (blockData instanceof Lightable lightable) {
                    lightable.setLit(configuration.getBoolean(path + "block-data-meta.lit", false));
                }

                if (blockData instanceof MultipleFacing multipleFacing) {
                    Set<BlockFace> faces = multipleFacing.getAllowedFaces();
                    for (BlockFace face : faces) {
                        multipleFacing.setFace(face, configuration.getBoolean(path + "block-data-meta.face." + face.name(), false));
                    }
                }

                if (blockData instanceof NoteBlock noteBlock) {
                    String instrument = configuration.getString(path + "block-data-meta.instrument");
                    if (instrument != null) {
                        noteBlock.setInstrument(Instrument.valueOf(instrument));
                    }
                    noteBlock.setNote(new Note(configuration.getInt(path + "block-data-meta.note", 0)));
                }

                if (blockData instanceof PinkPetals pinkPetals) {
                    pinkPetals.setFlowerAmount(configuration.getInt(path + "block-data-meta.flower-amount", 1));
                }

                if (blockData instanceof Piston piston) {
                    piston.setExtended(configuration.getBoolean(path + "block-data-meta.extended", false));
                }

                if (blockData instanceof PistonHead pistonHead) {
                    pistonHead.setShort(configuration.getBoolean(path + "block-data-meta.short", false));
                }

                if (blockData instanceof PointedDripstone pointedDripstone) {
                    String thickness = configuration.getString(path + "block-data-meta.thickness");
                    if (thickness != null) {
                        pointedDripstone.setThickness(PointedDripstone.Thickness.valueOf(thickness));
                    }
                    String verticalDirection = configuration.getString(path + "block-data-meta.vertical-direction");
                    if (verticalDirection != null) {
                        pointedDripstone.setVerticalDirection(BlockFace.valueOf(verticalDirection));
                    }
                }

                if (blockData instanceof Rail rail) {
                    String shape = configuration.getString(path + "block-data-meta.rail-shape");
                    if (shape != null) {
                        rail.setShape(Rail.Shape.valueOf(shape));
                    }
                }

                if (blockData instanceof Repeater repeater) {
                    repeater.setDelay(between(configuration.getInt(path + "block-data-meta.repeater-delay", 1), repeater.getMinimumDelay(), repeater.getMaximumDelay()));
                    repeater.setLocked(configuration.getBoolean(path + "block-data-meta.repeater-locked", false));
                }

                if (blockData instanceof RespawnAnchor respawnAnchor) {
                    respawnAnchor.setCharges(configuration.getInt(path + "block-data-meta.charges", 0));
                }

                if (blockData instanceof Rotatable rotatable) {
                    String rotation = configuration.getString(path + "block-data-meta.rotation");
                    if (rotation != null) {
                        rotatable.setRotation(BlockFace.valueOf(rotation));
                    }
                }

                if (blockData instanceof Scaffolding scaffolding) {
                    scaffolding.setBottom(configuration.getBoolean(path + "block-data-meta.scaffolding-bottom", false));
                    scaffolding.setDistance(configuration.getInt(path + "block-data-meta.scaffolding-distance", 0));
                }

                if (blockData instanceof SculkCatalyst sculkCatalyst) {
                    sculkCatalyst.setBloom(configuration.getBoolean(path + "block-data-meta.bloom", false));
                }

                if (blockData instanceof SculkSensor sculkSensor) {
                    String phase = configuration.getString(path + "block-data-meta.phase");
                    if (phase != null) {
                        sculkSensor.setPhase(SculkSensor.Phase.valueOf(phase));
                    }
                }

                if (blockData instanceof SculkShrieker sculkShrieker) {
                    sculkShrieker.setShrieking(configuration.getBoolean(path + "block-data-meta.shrieker-shrieking", false));
                    sculkShrieker.setCanSummon(configuration.getBoolean(path + "block-data-meta.shrieker-can-summon", false));
                }

                if (blockData instanceof SeaPickle seaPickle) {
                    seaPickle.setPickles(between(configuration.getInt(path + "block-data-meta.pickles", 1), seaPickle.getMinimumPickles(), seaPickle.getMaximumPickles()));
                }

                if (blockData instanceof Slab slab) {
                    String type = configuration.getString(path + "block-data-meta.slab-type");
                    if (type != null) {
                        slab.setType(Slab.Type.valueOf(type));
                    }
                }

                if (blockData instanceof Snow snow) {
                    snow.setLayers(between(configuration.getInt(path + "block-data-meta.layers", 1), snow.getMinimumLayers(), snow.getMaximumLayers()));
                }

                if (blockData instanceof Snowable snowable) {
                    snowable.setSnowy(configuration.getBoolean(path + "block-data-meta.snowy", false));
                }

                if (blockData instanceof Stairs stairs) {
                    String shape = configuration.getString(path + "block-data-meta.stairs-shape");
                    if (shape != null) {
                        stairs.setShape(Stairs.Shape.valueOf(shape));
                    }
                }

                if (blockData instanceof TechnicalPiston technicalPiston) {
                    String type = configuration.getString(path + "block-data-meta.technical-piston-type");
                    if (type != null) {
                        technicalPiston.setType(TechnicalPiston.Type.valueOf(type));
                    }
                }

                if (blockData instanceof TNT tnt) {
                    tnt.setUnstable(configuration.getBoolean(path + "block-data-meta.unstable", false));
                }

                if (blockData instanceof TrapDoor trapDoor) {
                    trapDoor.setOpen(configuration.getBoolean(path + "block-data-meta.open", false));
                }

                if (blockData instanceof Tripwire tripwire) {
                    tripwire.setDisarmed(configuration.getBoolean(path + "block-data-meta.disarmed", false));
                }

                if (blockData instanceof TripwireHook tripwireHook) {
                    tripwireHook.setAttached(configuration.getBoolean(path + "block-data-meta.attached", false));
                    tripwireHook.setPowered(configuration.getBoolean(path + "block-data-meta.powered", false));
                }

                if (blockData instanceof TrialSpawner trialSpawner) {
                    String state = configuration.getString(path + "block-data-meta.trial-spawner-state");
                    if (state != null) {
                        trialSpawner.setTrialSpawnerState(TrialSpawner.State.valueOf(state));
                    }
                    trialSpawner.setOminous(configuration.getBoolean(path + "block-data-meta.trial-spawner-ominous", false));
                }

                if (blockData instanceof TurtleEgg turtleEgg) {
                    turtleEgg.setEggs(between(configuration.getInt(path + "block-data-meta.eggs", 1), turtleEgg.getMinimumEggs(), turtleEgg.getMaximumEggs()));
                }


                if (blockData instanceof Vault vault) {
                    String state = configuration.getString(path + "block-data-meta.vault-state");
                    if (state != null) {
                        vault.setTrialSpawnerState(Vault.State.valueOf(state));
                    }
                    vault.setOminous(configuration.getBoolean(path + "block-data-meta.vault-ominous", false));
                }

                if (blockData instanceof Wall wall) {
                    wall.setUp(configuration.getBoolean(path + "block-data-meta.wall-up", false));
                    for (BlockFace face : BlockFace.values()) {
                        String height = configuration.getString(path + "block-data-meta.wall-height." + face.name());
                        if (height != null) {
                            wall.setHeight(face, Wall.Height.valueOf(height));
                        }
                    }
                }


            } catch (Exception exception) {
                plugin.getLogger().severe("Invalid block data or material configuration in " + fileName);
                exception.printStackTrace();
                enableBlockDataMeta = false;
            }
            this.blockDataMetaConfiguration = new BlockDataMetaConfiguration(enableBlockDataMeta, blockData);
        } else {
            this.blockDataMetaConfiguration = new BlockDataMetaConfiguration(false, null);
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
}
