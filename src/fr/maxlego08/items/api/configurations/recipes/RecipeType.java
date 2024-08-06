package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.ItemsPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

public enum RecipeType {

    CRAFTING_SHAPED,
    CRAFTING_SHAPELESS,
    BLASTING,
    CAMPFIRE_COOOKING,
    SMOKING,
    STONECUTTING,
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
}
