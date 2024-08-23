package fr.maxlego08.items.api.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.List;

public class TagRegistry {

    /*private static final Map<String, Tag<Material>> tagMap = new HashMap<>();

    static {
        register("ACACIA_LOGS", Tag.ACACIA_LOGS);
        register("AIR", Tag.AIR);
        register("ALL_HANGING_SIGNS", Tag.ALL_HANGING_SIGNS);
        register("ALL_SIGNS", Tag.ALL_SIGNS);
        register("ANCIENT_CITY_REPLACEABLE", Tag.ANCIENT_CITY_REPLACEABLE);
        register("ANIMALS_SPAWNABLE_ON", Tag.ANIMALS_SPAWNABLE_ON);
        register("ANVIL", Tag.ANVIL);
        register("ARMADILLO_SPAWNABLE_ON", Tag.ARMADILLO_SPAWNABLE_ON);
        register("AXOLOTLS_SPAWNABLE_ON", Tag.AXOLOTLS_SPAWNABLE_ON);
        register("AZALEA_GROWS_ON", Tag.AZALEA_GROWS_ON);
        register("AZALEA_ROOT_REPLACEABLE", Tag.AZALEA_ROOT_REPLACEABLE);
        register("BADLANDS_TERRACOTTA", Tag.BADLANDS_TERRACOTTA);
        register("BAMBOO_BLOCKS", Tag.BAMBOO_BLOCKS);
        register("BAMBOO_PLANTABLE_ON", Tag.BAMBOO_PLANTABLE_ON);
        register("BANNERS", Tag.BANNERS);
        register("BASE_STONE_NETHER", Tag.BASE_STONE_NETHER);
        register("BASE_STONE_OVERWORLD", Tag.BASE_STONE_OVERWORLD);
        register("BEACON_BASE_BLOCKS", Tag.BEACON_BASE_BLOCKS);
        register("BEDS", Tag.BEDS);
        register("BEE_GROWABLES", Tag.BEE_GROWABLES);
        register("BEEHIVES", Tag.BEEHIVES);
        register("BIG_DRIPLEAF_PLACEABLE", Tag.BIG_DRIPLEAF_PLACEABLE);
        register("BIRCH_LOGS", Tag.BIRCH_LOGS);
        register("BLOCKS_WIND_CHARGE_EXPLOSIONS", Tag.BLOCKS_WIND_CHARGE_EXPLOSIONS);
        register("BUTTONS", Tag.BUTTONS);
        register("CAMEL_SAND_STEP_SOUND_BLOCKS", Tag.CAMEL_SAND_STEP_SOUND_BLOCKS);
        register("CAMPFIRES", Tag.CAMPFIRES);
        register("CANDLE_CAKES", Tag.CANDLE_CAKES);
        register("CANDLES", Tag.CANDLES);
        register("CAULDRONS", Tag.CAULDRONS);
        register("CAVE_VINES", Tag.CAVE_VINES);
        register("CEILING_HANGING_SIGNS", Tag.CEILING_HANGING_SIGNS);
        register("CHERRY_LOGS", Tag.CHERRY_LOGS);
        register("CLIMBABLE", Tag.CLIMBABLE);
        register("CLUSTER_MAX_HARVESTABLES", Tag.CLUSTER_MAX_HARVESTABLES);
        register("COAL_ORES", Tag.COAL_ORES);
        register("COMBINATION_STEP_SOUND_BLOCKS", Tag.COMBINATION_STEP_SOUND_BLOCKS);
        register("COMPLETES_FIND_TREE_TUTORIAL", Tag.COMPLETES_FIND_TREE_TUTORIAL);
        register("CONCRETE_POWDER", Tag.CONCRETE_POWDER);
        register("CONVERTABLE_TO_MUD", Tag.CONVERTABLE_TO_MUD);
        register("COPPER_ORES", Tag.COPPER_ORES);
        register("CORAL_BLOCKS", Tag.CORAL_BLOCKS);
        register("CORAL_PLANTS", Tag.CORAL_PLANTS);
        register("CORALS", Tag.CORALS);
        register("CRIMSON_STEMS", Tag.CRIMSON_STEMS);
        register("CROPS", Tag.CROPS);
        register("CRYSTAL_SOUND_BLOCKS", Tag.CRYSTAL_SOUND_BLOCKS);
        register("DAMPENS_VIBRATIONS", Tag.DAMPENS_VIBRATIONS);
        register("DARK_OAK_LOGS", Tag.DARK_OAK_LOGS);
        register("DEAD_BUSH_MAY_PLACE_ON", Tag.DEAD_BUSH_MAY_PLACE_ON);
        register("DEEPSLATE_ORE_REPLACEABLES", Tag.DEEPSLATE_ORE_REPLACEABLES);
        register("DIAMOND_ORES", Tag.DIAMOND_ORES);
        register("DIRT", Tag.DIRT);
        register("DOES_NOT_BLOCK_HOPPERS", Tag.DOES_NOT_BLOCK_HOPPERS);
        register("DOORS", Tag.DOORS);
        register("DRAGON_IMMUNE", Tag.DRAGON_IMMUNE);
        register("DRAGON_TRANSPARENT", Tag.DRAGON_TRANSPARENT);
        register("DRIPSTONE_REPLACEABLE", Tag.DRIPSTONE_REPLACEABLE);
        register("EMERALD_ORES", Tag.EMERALD_ORES);
        register("ENCHANTMENT_POWER_PROVIDER", Tag.ENCHANTMENT_POWER_PROVIDER);
        register("ENCHANTMENT_POWER_TRANSMITTER", Tag.ENCHANTMENT_POWER_TRANSMITTER);
        register("ENDERMAN_HOLDABLE", Tag.ENDERMAN_HOLDABLE);
        register("FALL_DAMAGE_RESETTING", Tag.FALL_DAMAGE_RESETTING);
        register("FEATURES_CANNOT_REPLACE", Tag.FEATURES_CANNOT_REPLACE);
        register("FENCE_GATES", Tag.FENCE_GATES);
        register("FENCES", Tag.FENCES);
        register("FIRE", Tag.FIRE);
        register("FLOWER_POTS", Tag.FLOWER_POTS);
        register("FLOWERS", Tag.FLOWERS);
        register("FOXES_SPAWNABLE_ON", Tag.FOXES_SPAWNABLE_ON);
        register("FROG_PREFER_JUMP_TO", Tag.FROG_PREFER_JUMP_TO);
        register("FROGS_SPAWNABLE_ON", Tag.FROGS_SPAWNABLE_ON);
        register("GEODE_INVALID_BLOCKS", Tag.GEODE_INVALID_BLOCKS);
        register("GOATS_SPAWNABLE_ON", Tag.GOATS_SPAWNABLE_ON);
        register("GOLD_ORES", Tag.GOLD_ORES);
        register("GUARDED_BY_PIGLINS", Tag.GUARDED_BY_PIGLINS);
        register("HOGLIN_REPELLENTS", Tag.HOGLIN_REPELLENTS);
        register("ICE", Tag.ICE);
        register("IMPERMEABLE", Tag.IMPERMEABLE);
        register("INCORRECT_FOR_DIAMOND_TOOL", Tag.INCORRECT_FOR_DIAMOND_TOOL);
        register("INCORRECT_FOR_GOLD_TOOL", Tag.INCORRECT_FOR_GOLD_TOOL);
        register("INCORRECT_FOR_IRON_TOOL", Tag.INCORRECT_FOR_IRON_TOOL);
        register("INCORRECT_FOR_NETHERITE_TOOL", Tag.INCORRECT_FOR_NETHERITE_TOOL);
        register("INCORRECT_FOR_STONE_TOOL", Tag.INCORRECT_FOR_STONE_TOOL);
        register("INCORRECT_FOR_WOODEN_TOOL", Tag.INCORRECT_FOR_WOODEN_TOOL);
        register("INFINIBURN_END", Tag.INFINIBURN_END);
        register("INFINIBURN_NETHER", Tag.INFINIBURN_NETHER);
        register("INFINIBURN_OVERWORLD", Tag.INFINIBURN_OVERWORLD);
        register("INSIDE_STEP_SOUND_BLOCKS", Tag.INSIDE_STEP_SOUND_BLOCKS);
        register("INVALID_SPAWN_INSIDE", Tag.INVALID_SPAWN_INSIDE);
        register("IRON_ORES", Tag.IRON_ORES);
        register("ITEMS_PICKAXES", Tag.ITEMS_PICKAXES);
        register("JUNGLE_LOGS", Tag.JUNGLE_LOGS);
        register("LAPIS_ORES", Tag.LAPIS_ORES);
        register("LAVA_POOL_STONE_CANNOT_REPLACE", Tag.LAVA_POOL_STONE_CANNOT_REPLACE);
        register("LEAVES", Tag.LEAVES);
        register("LOGS", Tag.LOGS);
        register("LOGS_THAT_BURN", Tag.LOGS_THAT_BURN);
        register("LUSH_GROUND_REPLACEABLE", Tag.LUSH_GROUND_REPLACEABLE);
        register("MAINTAINS_FARMLAND", Tag.MAINTAINS_FARMLAND);
        register("MANGROVE_LOGS", Tag.MANGROVE_LOGS);
        register("MANGROVE_LOGS_CAN_GROW_THROUGH", Tag.MANGROVE_LOGS_CAN_GROW_THROUGH);
        register("MANGROVE_ROOTS_CAN_GROW_THROUGH", Tag.MANGROVE_ROOTS_CAN_GROW_THROUGH);
        register("MINEABLE_AXE", Tag.MINEABLE_AXE);
        register("MINEABLE_HOE", Tag.MINEABLE_HOE);
        register("MINEABLE_PICKAXE", Tag.MINEABLE_PICKAXE);
        register("MINEABLE_SHOVEL", Tag.MINEABLE_SHOVEL);
        register("MOB_INTERACTABLE_DOORS", Tag.MOB_INTERACTABLE_DOORS);
        register("MOOSHROOMS_SPAWNABLE_ON", Tag.MOOSHROOMS_SPAWNABLE_ON);
        register("MOSS_REPLACEABLE", Tag.MOSS_REPLACEABLE);
        register("MUSHROOM_GROW_BLOCK", Tag.MUSHROOM_GROW_BLOCK);
        register("NEEDS_DIAMOND_TOOL", Tag.NEEDS_DIAMOND_TOOL);
        register("NEEDS_IRON_TOOL", Tag.NEEDS_IRON_TOOL);
        register("NEEDS_STONE_TOOL", Tag.NEEDS_STONE_TOOL);
        register("NETHER_CARVER_REPLACEABLES", Tag.NETHER_CARVER_REPLACEABLES);
        register("NYLIUM", Tag.NYLIUM);
        register("OAK_LOGS", Tag.OAK_LOGS);
        register("OCCLUDES_VIBRATION_SIGNALS", Tag.OCCLUDES_VIBRATION_SIGNALS);
        register("OVERWORLD_CARVER_REPLACEABLES", Tag.OVERWORLD_CARVER_REPLACEABLES);
        register("PARROTS_SPAWNABLE_ON", Tag.PARROTS_SPAWNABLE_ON);
        register("PIGLIN_REPELLENTS", Tag.PIGLIN_REPELLENTS);
        register("PLANKS", Tag.PLANKS);
        register("POLAR_BEARS_SPAWNABLE_ON_ALTERNATE", Tag.POLAR_BEARS_SPAWNABLE_ON_ALTERNATE);
        register("PORTALS", Tag.PORTALS);
        register("PRESSURE_PLATES", Tag.PRESSURE_PLATES);
        register("PREVENT_MOB_SPAWNING_INSIDE", Tag.PREVENT_MOB_SPAWNING_INSIDE);
        register("RABBITS_SPAWNABLE_ON", Tag.RABBITS_SPAWNABLE_ON);
        register("RAILS", Tag.RAILS);
        register("REDSTONE_ORES", Tag.REDSTONE_ORES);
        register("REPLACEABLE", Tag.REPLACEABLE);
        register("REPLACEABLE_BY_TREES", Tag.REPLACEABLE_BY_TREES);
        register("SAND", Tag.SAND);
        register("SAPLINGS", Tag.SAPLINGS);
        register("SCULK_REPLACEABLE", Tag.SCULK_REPLACEABLE);
        register("SCULK_REPLACEABLE_WORLD_GEN", Tag.SCULK_REPLACEABLE_WORLD_GEN);
        register("SHULKER_BOXES", Tag.SHULKER_BOXES);
        register("SIGNS", Tag.SIGNS);
        register("SLABS", Tag.SLABS);
        register("SMALL_DRIPLEAF_PLACEABLE", Tag.SMALL_DRIPLEAF_PLACEABLE);
        register("SMALL_FLOWERS", Tag.SMALL_FLOWERS);
        register("SMELTS_TO_GLASS", Tag.SMELTS_TO_GLASS);
        register("SNAPS_GOAT_HORN", Tag.SNAPS_GOAT_HORN);
        register("SNIFFER_DIGGABLE_BLOCK", Tag.SNIFFER_DIGGABLE_BLOCK);
        register("SNIFFER_EGG_HATCH_BOOST", Tag.SNIFFER_EGG_HATCH_BOOST);
        register("SNOW", Tag.SNOW);
        register("SNOW_LAYER_CAN_SURVIVE_ON", Tag.SNOW_LAYER_CAN_SURVIVE_ON);
        register("SNOW_LAYER_CANNOT_SURVIVE_ON", Tag.SNOW_LAYER_CANNOT_SURVIVE_ON);
        register("SOUL_FIRE_BASE_BLOCKS", Tag.SOUL_FIRE_BASE_BLOCKS);
        register("SOUL_SPEED_BLOCKS", Tag.SOUL_SPEED_BLOCKS);
        register("SPRUCE_LOGS", Tag.SPRUCE_LOGS);
        register("STAIRS", Tag.STAIRS);
        register("STANDING_SIGNS", Tag.STANDING_SIGNS);
        register("STONE_BRICKS", Tag.STONE_BRICKS);
        register("STONE_BUTTONS", Tag.STONE_BUTTONS);
        register("STONE_ORE_REPLACEABLES", Tag.STONE_ORE_REPLACEABLES);
        register("STONE_PRESSURE_PLATES", Tag.STONE_PRESSURE_PLATES);
        register("STRIDER_WARM_BLOCKS", Tag.STRIDER_WARM_BLOCKS);
        register("SWORD_EFFICIENT", Tag.SWORD_EFFICIENT);
        register("TALL_FLOWERS", Tag.TALL_FLOWERS);
        register("TERRACOTTA", Tag.TERRACOTTA);
        register("TRAIL_RUINS_REPLACEABLE", Tag.TRAIL_RUINS_REPLACEABLE);
        register("TRAPDOORS", Tag.TRAPDOORS);
        register("UNDERWATER_BONEMEALS", Tag.UNDERWATER_BONEMEALS);
        register("UNSTABLE_BOTTOM_CENTER", Tag.UNSTABLE_BOTTOM_CENTER);
        register("VALID_SPAWN", Tag.VALID_SPAWN);
        register("VIBRATION_RESONATORS", Tag.VIBRATION_RESONATORS);
        register("WALL_CORALS", Tag.WALL_CORALS);
        register("WALL_HANGING_SIGNS", Tag.WALL_HANGING_SIGNS);
        register("WALL_POST_OVERRIDE", Tag.WALL_POST_OVERRIDE);
        register("WALL_SIGNS", Tag.WALL_SIGNS);
        register("WALLS", Tag.WALLS);
        register("WARPED_STEMS", Tag.WARPED_STEMS);
        register("WART_BLOCKS", Tag.WART_BLOCKS);
        register("WITHER_IMMUNE", Tag.WITHER_IMMUNE);
        register("WITHER_SUMMON_BASE_BLOCKS", Tag.WITHER_SUMMON_BASE_BLOCKS);
        register("WOLVES_SPAWNABLE_ON", Tag.WOLVES_SPAWNABLE_ON);
        register("WOODEN_BUTTONS", Tag.WOODEN_BUTTONS);
        register("WOODEN_DOORS", Tag.WOODEN_DOORS);
        register("WOODEN_FENCES", Tag.WOODEN_FENCES);
        register("WOODEN_PRESSURE_PLATES", Tag.WOODEN_PRESSURE_PLATES);
        register("WOODEN_SLABS", Tag.WOODEN_SLABS);
        register("WOODEN_STAIRS", Tag.WOODEN_STAIRS);
        register("WOODEN_TRAPDOORS", Tag.WOODEN_TRAPDOORS);
        register("WOOL", Tag.WOOL);
        register("WOOL_CARPETS", Tag.WOOL_CARPETS);
    }

    public static void register(String key, Tag<Material> tag) {
        tagMap.put(key, tag);
    }

    public static Tag<Material> getTags(String key) {
        return tagMap.get(key);
    }*/

    public static Tag<Material> getTag(String key) {
        for (String type : List.of(Tag.REGISTRY_BLOCKS, Tag.REGISTRY_ITEMS)) {
            Tag<Material> tag = Bukkit.getTag(type.trim().toLowerCase(), NamespacedKey.minecraft(key.toLowerCase()), Material.class);
            if (tag != null) {
                return tag;
            }
        }
        throw new IllegalArgumentException("Invalid tag type");
    }
}