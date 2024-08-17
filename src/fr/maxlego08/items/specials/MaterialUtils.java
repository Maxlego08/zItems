package fr.maxlego08.items.specials;

import org.bukkit.Material;

import java.util.EnumSet;
import java.util.Set;

public class MaterialUtils {

    public static final Set<Material> shovels = EnumSet.of(
            Material.WOODEN_SHOVEL, Material.GOLDEN_SHOVEL, Material.STONE_SHOVEL,
            Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL
    );

    public static final Set<Material> pickaxes = EnumSet.of(
            Material.WOODEN_PICKAXE, Material.GOLDEN_PICKAXE, Material.STONE_PICKAXE,
            Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE
    );

    public static final Set<Material> axes = EnumSet.of(
            Material.WOODEN_AXE, Material.GOLDEN_AXE, Material.STONE_AXE,
            Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE
    );

    public static final Set<Material> hoes = EnumSet.of(
            Material.WOODEN_HOE, Material.GOLDEN_HOE, Material.STONE_HOE,
            Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE
    );

    public static final Set<Material> crops = EnumSet.of(
            Material.WHEAT, Material.POTATOES, Material.CARROTS,
            Material.BEETROOTS, Material.NETHER_WART);

    public static final Set<Material> logs = EnumSet.of(
            Material.ACACIA_LOG, Material.BIRCH_LOG, Material.DARK_OAK_LOG, Material.JUNGLE_LOG, Material.OAK_LOG,
            Material.SPRUCE_LOG, Material.WARPED_STEM, Material.CRIMSON_STEM, Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_CRIMSON_HYPHAE, Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_SPRUCE_LOG, Material.MANGROVE_LOG,
            Material.BROWN_MUSHROOM_BLOCK, Material.RED_MUSHROOM_BLOCK, Material.MUSHROOM_STEM, Material.CHERRY_LOG,
            Material.STRIPPED_CHERRY_LOG);

    public static boolean isShovel(Material material) {
        return shovels.contains(material);
    }

    public static boolean isPickaxe(Material material) {
        return pickaxes.contains(material);
    }

    public static boolean isAxe(Material material) {
        return axes.contains(material);
    }

    public static boolean isHoe(Material material) {
        return hoes.contains(material);
    }

    public static boolean isCrop(Material material) {
        return crops.contains(material);
    }

    public static boolean isLog(Material material) {
        return logs.contains(material);
    }


}