package fr.maxlego08.items.api.configurations;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public record RecipeConfiguration(String[] pattern, Map<String, String> ingredients) {

    public static RecipeConfiguration loadRecipe(YamlConfiguration configuration, String fileName, String path) {
        ConfigurationSection craftSection = configuration.getConfigurationSection(path + "craft");
        boolean enableRecipe = craftSection != null;

        if (!enableRecipe) {
            return new RecipeConfiguration(null, null);
        }

        List<String> pattern = (List<String>) craftSection.getList("pattern", new ArrayList<>());
        if(pattern.isEmpty()) {
            throw new IllegalArgumentException("Empty pattern for item " + fileName);
        }
        if(pattern.size() > 3 || pattern.stream().anyMatch(s -> s.split("").length > 3)) {
            throw new IllegalArgumentException("Pattern size is too big for item " + fileName);
        }
        if(pattern.stream().map(String::length).distinct().count() > 1) {
            throw new IllegalArgumentException("Pattern size is not consistent for item " + fileName);
        }
        if(pattern.stream().anyMatch(String::isEmpty)) {
            throw new IllegalArgumentException("Pattern contains empty string for item " + fileName);
        }

        ConfigurationSection ingredientsSection = craftSection.getConfigurationSection("ingredients");
        if(ingredientsSection == null) {
            throw new IllegalArgumentException("Missing ingredients section for item " + fileName);
        }
        Map<String, String> ingredients = new HashMap<>();
        for (String key : ingredientsSection.getKeys(false)) {
            ConfigurationSection ingredient = ingredientsSection.getConfigurationSection(key);
            if(pattern.stream().noneMatch(s -> s.contains(key))) {
                throw new IllegalArgumentException("Pattern does not contain key " + key + " for item " + fileName);
            }
            if(ingredient == null) {
                throw new IllegalArgumentException("Missing ingredient section for key " + key + " for item " + fileName);
            }
            String customItem = ingredient.getString("custom-item");
            String tag = ingredient.getString("tag");
            String material = ingredient.getString("material");

            if(customItem != null) {
                if(fileName.contains(customItem)) {
                    throw new IllegalArgumentException("Recipe contains itself for item " + fileName);
                }
                ingredients.put(key, "zitems|"+customItem);
            } else if(tag != null) {
                ingredients.put(key, "tag|"+tag);
            } else if(material != null) {
                ingredients.put(key, "material|"+material);
            } else {
                throw new IllegalArgumentException("Missing custom-item, tag or material for key " + key + " for item " + fileName);
            }

        }

        return new RecipeConfiguration(pattern.toArray(new String[0]), ingredients);
    }

    public void apply(ItemPlugin plugin, ItemStack result, String filename) {
        if(pattern == null || ingredients == null) {
            return;
        }

        NamespacedKey recipeKey = new NamespacedKey(plugin, filename + "_craft");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, result);
        recipe.shape(pattern);
        for (Map.Entry<String, String> entry : ingredients.entrySet()) {
            char key = entry.getKey().charAt(0);
            String[] ingredientArray = entry.getValue().split("\\|");
            String type = ingredientArray[0];
            String item = ingredientArray[1];
            switch (type) {
                case "zitems" -> {
                    Optional<Item> itemOpt = plugin.getItemManager().getItem(item);
                    if(itemOpt.isEmpty()) {
                        throw new IllegalArgumentException("Invalid custom-item " + item + " for key " + key + " for item " + filename);
                    }
                    recipe.setIngredient(key, new RecipeChoice.MaterialChoice(itemOpt.get().build(null, 1).getType()));
                }
                case "tag" -> {
                    //get in interface clas the equivalent of String
                    String[] tagArray = item.split(":");
                    if(tagArray.length != 2) {
                        throw new IllegalArgumentException("Invalid tag " + item + " for key " + key + " for item " + filename);
                    }
                    String tagType = tagArray[0];
                    String tagName = tagArray[1];
                    Tag<Material> tag = Bukkit.getTag(tagType.toLowerCase(), NamespacedKey.minecraft(tagName.toLowerCase()), Material.class);
                    if(tag == null) {
                        throw new IllegalArgumentException("Invalid tag " + item + " for key " + key + " for item " + filename);
                    }
                    recipe.setIngredient(key, new RecipeChoice.MaterialChoice(tag));
                }
                case "material" -> {
                    Material material = Material.valueOf(item.toUpperCase());
                    recipe.setIngredient(key, new RecipeChoice.MaterialChoice(material));
                }
                default -> throw new IllegalArgumentException("Invalid type " + type + " for key " + key + " for item " + filename);
            }
        }

        plugin.getServer().addRecipe(recipe);
    }
}
