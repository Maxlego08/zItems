package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.utils.Helper;
import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.RecipeChoice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record RecipeConfiguration(List<ItemRecipe> recipes) {

    public static RecipeConfiguration loadRecipe(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        List<ItemRecipe> recipes = new ArrayList<>();

        List<Map<String, Object>> recipesConfig = (List<Map<String, Object>>) configuration.getList(path + "recipes");

        if (recipesConfig != null) {
            for (Map<String, Object> recipeConfig : recipesConfig) {
                RecipeType recipeType = RecipeType.getRecipeByString(((String) recipeConfig.get("type")).toUpperCase());
                if (recipeType == null) {
                    plugin.getLogger().warning("Invalid recipe type in " + fileName + " at " + path + " for " + recipeConfig.get("type"));
                    continue;
                }
                int amount = (int) recipeConfig.getOrDefault("amount", 1);
                ItemRecipe recipe = null;
                switch (recipeType) {
                    case CRAFTING_SHAPED -> recipe = getShapedRecipe(plugin, recipeConfig, amount);
                    case CRAFTING_SHAPELESS -> recipe = getShapelessRecipe(plugin, recipeConfig, amount);
                    case BLASTING, CAMPFIRE_COOKING, SMOKING, SMELTING ->
                            recipe = allFurnaceTypeRecipe(recipeType, plugin, recipeConfig, amount);
                    case STONE_CUTTING -> recipe = getStonecuttingRecipe(plugin, recipeConfig, amount);
                    case SMITHING_TRANSFORM -> recipe = getSmithingTransformRecipe(plugin, recipeConfig, amount);
                }

                recipes.add(recipe);
            }
        }

        return new RecipeConfiguration(recipes);
    }

    private static ItemRecipe getSmithingTransformRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        ItemRecipe.Ingredient[] ingredients = new ItemRecipe.Ingredient[3];

        int i = 0;
        for (String key : List.of("template", "base", "addition")) {
            if (!recipeConfig.containsKey(key)) {
                throw new IllegalArgumentException("Missing key " + key + " in smithing transform recipe");
            }
            Map<String, String> ingredient = (Map<String, String>) recipeConfig.get(key);
            RecipeChoice choice = Helper.getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get(ingredient.keySet().toArray()[0]));
            ingredients[i++] = new ItemRecipe.Ingredient(choice, ingredient.get(ingredient.keySet().toArray()[0]), '-');
        }

        return new ItemRecipe("", "", RecipeType.SMITHING_TRANSFORM, amount, ingredients, new String[0], 0, 0);
    }

    private static ItemRecipe getStonecuttingRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        String group = (String) recipeConfig.getOrDefault("group", "");
        Map<String, String> ingredient = (Map<String, String>) recipeConfig.get("ingredient");
        RecipeChoice choice = Helper.getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get(ingredient.keySet().toArray()[0]));
        return new ItemRecipe(group, "", RecipeType.STONE_CUTTING, amount, new ItemRecipe.Ingredient[]{new ItemRecipe.Ingredient(choice, ingredient.get(ingredient.keySet().toArray()[0]), '-')}, new String[0], 0, 0);
    }

    private static ItemRecipe allFurnaceTypeRecipe(RecipeType type, ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        //TODO: if recipe with ingredient exist the existnt recipe doesn't work

        String group = (String) recipeConfig.getOrDefault("group", "");
        String category = (String) recipeConfig.getOrDefault("category", "");
        int cookingTime = (int) recipeConfig.getOrDefault("cooking-time", 200);
        float experience = ((Number) recipeConfig.getOrDefault("experience", 0)).floatValue();
        Map<String, String> ingredient = (Map<String, String>) recipeConfig.get("ingredient");
        RecipeChoice choice = Helper.getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get(ingredient.keySet().toArray()[0]));
        return new ItemRecipe(group, category, type, amount, new ItemRecipe.Ingredient[]{new ItemRecipe.Ingredient(choice, ingredient.get(ingredient.keySet().toArray()[0]), '-')}, new String[0], cookingTime, experience);
    }

    private static ItemRecipe getShapelessRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        String group = (String) recipeConfig.getOrDefault("group", "");
        String category = (String) recipeConfig.getOrDefault("category", "");
        List<ItemRecipe.Ingredient> ingredients = new ArrayList<>();

        List<Map<String, String>> ingredientsMap = (List<Map<String, String>>) recipeConfig.get("ingredients");
        for (Map<String, String> ingredient : ingredientsMap) {
            RecipeChoice choice = Helper.getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get(ingredient.keySet().toArray()[0]));
            ingredients.add(new ItemRecipe.Ingredient(choice, ingredient.get(ingredient.keySet().toArray()[0]), '-'));
        }
        return new ItemRecipe(group, category, RecipeType.CRAFTING_SHAPELESS, amount, ingredients.toArray(new ItemRecipe.Ingredient[0]), new String[0], 0, 0);
    }

    private static ItemRecipe getShapedRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        String group = (String) recipeConfig.getOrDefault("group", "");
        String category = (String) recipeConfig.getOrDefault("category", "");
        List<ItemRecipe.Ingredient> ingredients = new ArrayList<>();

        List<String> pattern = (List<String>) recipeConfig.get("pattern");
        Map<Object, Object> ingredientsMap = (Map<Object, Object>) recipeConfig.get("ingredients");

        for (Map.Entry<Object, Object> stringObjectEntry : ingredientsMap.entrySet()) {
            String sign = String.valueOf(stringObjectEntry.getKey());
            Map<String, String> ingredient = (Map<String, String>) stringObjectEntry.getValue();
            RecipeChoice choice = Helper.getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get(ingredient.keySet().toArray()[0]));
            ingredients.add(new ItemRecipe.Ingredient(choice, ingredient.get(ingredient.keySet().toArray()[0]), sign.charAt(0)));
        }

        return new ItemRecipe(group, category, RecipeType.CRAFTING_SHAPED, amount, ingredients.toArray(new ItemRecipe.Ingredient[0]), pattern.toArray(new String[0]), 0, 0);
    }

    public void apply(Item itemName, ItemsPlugin plugin) {
        for (ItemRecipe recipe : this.recipes) {
            var server = plugin.getServer();
            if(server.addRecipe(recipe.toBukkitRecipe(itemName))) {
                server.updateRecipes();
            }
        }
    }

    public void deleteRecipe(Item item, ItemsPlugin plugin) {
        for (ItemRecipe recipe : this.recipes) {
            var server = plugin.getServer();
            if(server.removeRecipe(recipe.getNamespacedKey(item))) {
                server.updateRecipes();
            }

        }
    }
}
