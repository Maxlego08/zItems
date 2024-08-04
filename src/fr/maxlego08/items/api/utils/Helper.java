package fr.maxlego08.items.api.utils;

import org.bukkit.Color;
import org.bukkit.configuration.file.YamlConfiguration;

public class Helper {

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
