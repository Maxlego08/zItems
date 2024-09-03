package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.ItemsPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public enum RecipeType {

    CRAFTING_SHAPED,
    CRAFTING_SHAPELESS,
    BLASTING,
    CAMPFIRE_COOKING,
    SMOKING,
    STONE_CUTTING,
    SMELTING,
    SMITHING_TRANSFORM,
    ;

    public NamespacedKey getNamespacedKey(String key) {
        return new NamespacedKey(JavaPlugin.getProvidingPlugin(ItemsPlugin.class), name().toLowerCase() + "_" + key);
    }

    public static RecipeType getRecipeByString(String name) {
        for (RecipeType recipeType : values()) {
            if (recipeType.name().equalsIgnoreCase(name)) {
                return recipeType;
            }
        }
        return null;
    }

    public static List<RecipeType> smeltingRecipes() {
        return List.of(CAMPFIRE_COOKING, BLASTING, SMOKING, SMELTING);
    }
}
