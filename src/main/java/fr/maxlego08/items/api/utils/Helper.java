package fr.maxlego08.items.api.utils;

import fr.maxlego08.items.api.Item;
import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class Helper {

    public static RecipeChoice getRecipeChoiceFromString(ItemPlugin plugin, String ingredient, String fileName) {
        String[] ingredientArray = ingredient.split("\\|");
        switch (ingredientArray[0].trim()) {
            case "item" -> {
                String[] data = ingredientArray[1].trim().split(":");
                if (data[0].equalsIgnoreCase("minecraft")) {
                    return new RecipeChoice.MaterialChoice(Material.valueOf(data[1].trim().toUpperCase()));
                } else if (data[0].equalsIgnoreCase("zitems")) {
                    Item ingredientItem = plugin.getItemManager().getItem(data[1].trim()).orElseThrow(() -> new IllegalArgumentException("Invalid item name"));
                    return new RecipeChoice.MaterialChoice(ingredientItem.build(null, 1).getType());
                } else if (data.length == 1) {
                    try {
                        return new RecipeChoice.MaterialChoice(Material.valueOf(data[0].trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid material for ingredient " + ingredient + " in " + fileName);
                    }
                } else {
                    throw new IllegalArgumentException("Invalid item type for ingredient " + ingredient + " in " + fileName);
                }
            }
            case "tag" -> {
                return new RecipeChoice.MaterialChoice(TagRegistry.getTag(ingredientArray[1].trim().toUpperCase()));
            }
            default -> throw new IllegalArgumentException("Invalid ingredient type for ingredient "+ ingredient + " in " + fileName);
        }
    }

    public static int between(int value, int min, int max) {
        return value > max ? max : Math.max(value, min);
    }

    public static Color getColor(YamlConfiguration configuration, String path, Color defaultColor) {

        try {
            String[] split = configuration.getString(path, "").split(",");

            if (split.length == 3) {
                return Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            } else if (split.length == 4) {
                return Color.fromARGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
            }
        } catch (Exception ignored) {
        }

        return defaultColor;
    }


}
