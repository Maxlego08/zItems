package fr.maxlego08.items.api.configurations.meta;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public record LeatherArmorMetaConfiguration(Color color) {

    public static LeatherArmorMetaConfiguration loadLeatherArmorMetaConfiguration(YamlConfiguration configuration, String fileName, String path) {
        Color color = null;

        boolean hasColor = configuration.contains(path + "leather-armor.color");

        if(hasColor) {
            String colorString = configuration.getString(path + "leather-armor.color", "#FFFFFF");
            if (colorString.contains(",")) {
                String[] split = colorString.split(",");
                if(split.length != 3) {
                    throw new IllegalArgumentException("Invalid color format for " + path + ".leather-armor.color in " + fileName);
                }
                color = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
            } else if(colorString.contains("#")) {
                color = Color.fromRGB(Integer.parseInt(colorString.replace("#", ""), 16));
            } else {
                throw new IllegalArgumentException("Invalid color format for " + path + ".leather-armor.color in " + fileName);
            }
        }

        return new LeatherArmorMetaConfiguration(color);
    }

    public void apply(LeatherArmorMeta leatherArmorMeta) {
        if(color != null)
            leatherArmorMeta.setColor(color);
    }

}
