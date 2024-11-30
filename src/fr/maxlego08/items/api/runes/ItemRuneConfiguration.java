package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.applicators.ApplicatorType;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

public record ItemRuneConfiguration(boolean enableCrafting, Rune rune, ApplicatorType type, List<String> ingredientList, List<String> extraIngredients) {

    public static ItemRuneConfiguration loadItemRuneConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        var enableApplicator = configuration.getBoolean(path + "rune-representation.enable-applicator", false);
        ApplicatorType applicatorType = ApplicatorType.valueOf(configuration.getString(path + "rune-representation.applicator", "NONE").toUpperCase());

        List<String> ingredients = new ArrayList<>();
        List<String> extras = new ArrayList<>();
        switch (applicatorType) {
            case SMITHING_TABLE -> {
                String template = configuration.getString(path + "rune-representation.template", "ERROR");
                if(template.equals("ERROR")) {
                    throw new IllegalArgumentException("Template is not defined for the item " + fileName);
                }
                ingredients.add(template);
            }
            case ZITEMS_APPLICATOR -> {
                var ingredientList = configuration.getStringList(path + "rune-representation.inputs");
                if(ingredientList.isEmpty()) {
                    throw new IllegalArgumentException("Ingredients are not defined for the item " + fileName);
                }
                ingredients.addAll(ingredientList);
                var extraIngredients = configuration.getStringList(path + "rune-representation.extra-inputs");
                extras.addAll(extraIngredients);
            }
        }

        var runeName = configuration.getString(path + "rune-representation.type", "ERROR").toLowerCase();
        Rune rune = plugin.getRuneManager().getRune(runeName)
                .orElseThrow(() -> new IllegalArgumentException("Rune " + runeName + " was not found for the item " + fileName));

        return new ItemRuneConfiguration(enableApplicator, rune, applicatorType, ingredients, extras);
    }



}
