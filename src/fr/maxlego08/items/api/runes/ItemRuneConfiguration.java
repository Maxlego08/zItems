package fr.maxlego08.items.api.runes;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public record ItemRuneConfiguration(boolean enableCrafting, Rune rune, String template) {

    public static ItemRuneConfiguration loadItemRuneConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String fileName, String path) {

        boolean enableCrafting = configuration.getBoolean(path + "enable-crafting", true);

        if(!configuration.contains(path + "represent-rune")) {
            throw new IllegalArgumentException("The item " + fileName + " does not have a represent-rune");
        }

        var runeName = configuration.getString(path + "represent-rune").toLowerCase();
        Rune rune = plugin.getRuneManager().getRune(runeName)
                .orElseThrow(() -> new IllegalArgumentException("Rune " + runeName + " was not found for the item " + fileName));

        if(enableCrafting && !configuration.contains(path + "template")) {
            throw new IllegalArgumentException("The item " + fileName + " does not have a template");
        }

        String template = configuration.getString(path + "template", "ERROR").toLowerCase();

        return new ItemRuneConfiguration(enableCrafting, rune, template);
    }

}
