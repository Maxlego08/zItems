package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

public record ItemRecipe(RecipeType recipeType, int amount, Ingredient[] ingredients, int cookingTime, float experience) {

    record Ingredient(RecipeChoice choice, char sign) {}

    public Recipe toBukkitRecipe(Item item) {
        NamespacedKey key = recipeType.getNamespacedKey(item.getName());
        ItemStack result = item.build(null, amount);
        Recipe recipe = null;
        switch (recipeType) {
            case CRAFTING_SHAPED -> {
                recipe = new ShapedRecipe(key, result);
                for (Ingredient ingredient : ingredients) {
                    ((ShapedRecipe) recipe).setIngredient(ingredient.sign(), ingredient.choice());
                }
            }
            case CRAFTING_SHAPELESS -> {
                recipe = new ShapelessRecipe(key, result);
                for (Ingredient ingredient : ingredients) {
                    ((ShapelessRecipe) recipe).addIngredient(ingredient.choice());
                }
            }
            case BLASTING -> {
                recipe = new BlastingRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
            }
            case CAMPFIRE_COOOKING -> {
                recipe = new CampfireRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
            }
            case SMOKING -> {
                recipe = new SmokingRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
            }
            case STONECUTTING -> {
                recipe = new StonecuttingRecipe(key, result, ingredients[0].choice());
            }
            case SMELTING -> {
                recipe = new FurnaceRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
            }
            case SMITHING_TRANSFORM -> {
                recipe = new SmithingTransformRecipe(key, result, ingredients[0].choice(), ingredients[1].choice(), ingredients[2].choice());
            }
        }
        return recipe;
    }
}
