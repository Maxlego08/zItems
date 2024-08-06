package fr.maxlego08.items.api.configurations.recipes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.*;

import java.util.*;

public record RecipeConfiguration(List<ItemRecipe> recipes) {

    public static RecipeConfiguration loadRecipe(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {
        List<ItemRecipe> recipes = new ArrayList<>();

        List<Map<String, Object>> recipesConfig = (List<Map<String, Object>>) configuration.getList(path + "recipes");

        if(recipesConfig != null) {
            for (Map<String, Object> recipeConfig : recipesConfig) {
                RecipeType recipeType = RecipeType.getRecipeByString(((String) recipeConfig.get("type")).toUpperCase());
                if (recipeType == null) {
                    plugin.getLogger().warning("Invalid recipe type in " + fileName + " at " + path);
                    continue;
                }
                int amount = (int) recipeConfig.getOrDefault("amount", 1);
                ItemRecipe recipe = null;
                switch (recipeType) {
                    case CRAFTING_SHAPED -> {
                        recipe = getShapedRecipe(plugin, recipeConfig, amount);
                    }
                    case CRAFTING_SHAPELESS -> {
                        recipe = getShapelessRecipe(plugin, recipeConfig, amount);
                    }
                    case BLASTING, CAMPFIRE_COOOKING, SMOKING, SMELTING -> {
                        recipe = allFurnaceTypeRecipe(recipeType, plugin, recipeConfig, amount);
                    }
                    case STONECUTTING -> {
                        recipe = getSonecuttingRecipe(plugin, recipeConfig, amount);
                    }
                    case SMITHING_TRANSFORM -> {
                        recipe = getSmithingTransformRecipe(plugin, recipeConfig, amount);
                    }
                }

                recipes.add(recipe);
            }
        }

        return new RecipeConfiguration(recipes);
    }

    private static RecipeChoice getRecipeChoiceFromString(ItemPlugin plugin, String ingredient) {
        String[] ingredientArray = ingredient.split("\\|");
        switch (ingredientArray[0].trim()) {
            case "item" -> {
                String[] data = ingredientArray[1].trim().split(":");
                if(data[0].equalsIgnoreCase("minecraft")) {
                    return new RecipeChoice.MaterialChoice(Material.valueOf(data[1].trim().toUpperCase()));
                } else if(data[0].equalsIgnoreCase("zitems")) {
                    Item ingredientItem = plugin.getItemManager().getItem(data[1].trim()).orElseThrow(() -> new IllegalArgumentException("Invalid item name"));
                    new RecipeChoice.MaterialChoice(ingredientItem.build(null, 1).getType());
                } else {
                    throw new IllegalArgumentException("Invalid item type");
                }
            }
            case "tag" -> {
                for (String type : List.of(Tag.REGISTRY_BLOCKS, Tag.REGISTRY_ITEMS)) {
                    Tag<Material> tag = Bukkit.getTag(type.trim().toLowerCase(), NamespacedKey.minecraft(ingredientArray[1].trim().toLowerCase()), Material.class);
                    if(tag != null) {
                        return new RecipeChoice.MaterialChoice(tag);
                    }
                }
                throw new IllegalArgumentException("Invalid tag type");
            }
            default -> throw new IllegalArgumentException("Invalid ingredient type");
        }
        throw new IllegalArgumentException("Invalid ingredient type");
    }

    private static ItemRecipe getSmithingTransformRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        ItemRecipe.Ingredient[] ingredients = new ItemRecipe.Ingredient[3];

        int i = 0;
        for (String key : List.of("template", "base", "addition")) {
            if (!recipeConfig.containsKey(key)) {
                throw new IllegalArgumentException("Missing key " + key + " in smithing transform recipe");
            }
            Map<String, String> ingredient = (Map<String, String>) recipeConfig.get(key);
            RecipeChoice choice = getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get( ingredient.keySet().toArray()[0]));
            ingredients[i++] = new ItemRecipe.Ingredient(choice, '-');
        }

        return new ItemRecipe(RecipeType.SMITHING_TRANSFORM, amount, ingredients, 0, 0);
    }

    private static ItemRecipe getSonecuttingRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        return null;
    }

    private static ItemRecipe allFurnaceTypeRecipe(RecipeType type, ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        int cookingTime = (int) recipeConfig.getOrDefault("cooking-time", 200);
        float experience = ((Number) recipeConfig.getOrDefault("experience", 0)).floatValue();
        Map<String, String> ingredient = (Map<String, String>) recipeConfig.get("ingredient");
        RecipeChoice choice = getRecipeChoiceFromString(plugin, ingredient.keySet().toArray()[0] + "|" + ingredient.get( ingredient.keySet().toArray()[0]));
        return new ItemRecipe(type, amount, new ItemRecipe.Ingredient[]{new ItemRecipe.Ingredient(choice, '-')}, cookingTime, experience);
    }

    private static ItemRecipe getShapelessRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        return null;
    }

    private static ItemRecipe getShapedRecipe(ItemPlugin plugin, Map<String, Object> recipeConfig, int amount) {
        return null;
    }

    public void apply(Item itemName, ItemsPlugin plugin) {
        for (ItemRecipe recipe : recipes) {
            plugin.getServer().addRecipe(recipe.toBukkitRecipe(itemName));
        }
    }
}
