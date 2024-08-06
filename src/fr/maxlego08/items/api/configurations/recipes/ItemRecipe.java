package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.api.Item;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.recipe.CookingBookCategory;
import org.bukkit.inventory.recipe.CraftingBookCategory;

public record ItemRecipe(String group, String category, RecipeType recipeType, int amount, Ingredient[] ingredients, String[] pattern, int cookingTime, float experience) {

    record Ingredient(RecipeChoice choice, char sign) {}

    public Recipe toBukkitRecipe(Item item) {
        NamespacedKey key = recipeType.getNamespacedKey(item.getName());
        ItemStack result = item.build(null, amount);
        Recipe recipe = null;
        switch (recipeType) {
            case CRAFTING_SHAPED -> {
                recipe = new ShapedRecipe(key, result);
                ((ShapedRecipe) recipe).shape(pattern);
                for (Ingredient ingredient : ingredients) {
                    ((ShapedRecipe) recipe).setIngredient(ingredient.sign(), ingredient.choice());
                }
                if(!group.isEmpty()) {
                    ((ShapedRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((ShapedRecipe) recipe).setCategory(CraftingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case CRAFTING_SHAPELESS -> {
                recipe = new ShapelessRecipe(key, result);
                for (Ingredient ingredient : ingredients) {
                    ((ShapelessRecipe) recipe).addIngredient(ingredient.choice());
                }
                if(!group.isEmpty()) {
                    ((ShapelessRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((ShapelessRecipe) recipe).setCategory(CraftingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case BLASTING -> {
                recipe = new BlastingRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
                if(!group.isEmpty()) {
                    ((BlastingRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((BlastingRecipe) recipe).setCategory(CookingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case CAMPFIRE_COOOKING -> {
                recipe = new CampfireRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
                if(!group.isEmpty()) {
                    ((CampfireRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((CampfireRecipe) recipe).setCategory(CookingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case SMOKING -> {
                recipe = new SmokingRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
                if(!group.isEmpty()) {
                    ((SmokingRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((SmokingRecipe) recipe).setCategory(CookingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case STONECUTTING -> {
                recipe = new StonecuttingRecipe(key, result, ingredients[0].choice());
                if(!group.isEmpty()) {
                    ((StonecuttingRecipe) recipe).setGroup(group);
                }
            }
            case SMELTING -> {
                recipe = new FurnaceRecipe(key, result, ingredients[0].choice(), experience, cookingTime);
                if(!group.isEmpty()) {
                    ((FurnaceRecipe) recipe).setGroup(group);
                }
                if(!category.isEmpty()) {
                    ((FurnaceRecipe) recipe).setCategory(CookingBookCategory.valueOf(category.toUpperCase()));
                }
            }
            case SMITHING_TRANSFORM -> {
                recipe = new SmithingTransformRecipe(key, result, ingredients[0].choice(), ingredients[1].choice(), ingredients[2].choice());
            }
        }
        return recipe;
    }
}
