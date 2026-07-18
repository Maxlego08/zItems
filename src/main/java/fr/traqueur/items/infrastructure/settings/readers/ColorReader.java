package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.Color;

public class ColorReader implements Reader<Color> {
    @Override
    public Color read(String s) throws StructuraException {
        try {
            // Support hex format: #RRGGBB or RRGGBB
            if (s.startsWith("#")) {
                s = s.substring(1);
            }

            if (s.length() == 6) {
                int rgb = Integer.parseInt(s, 16);
                return Color.fromRGB(rgb);
            }

            // Support comma-separated RGB: "255,0,0"
            if (s.contains(",")) {
                String[] parts = s.split(",");
                if (parts.length != 3) {
                    throw new StructuraException("Invalid RGB color format: " + s + " (expected R,G,B)");
                }
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());
                return Color.fromRGB(r, g, b);
            }

            throw new StructuraException("Invalid color format: " + s + " (use #RRGGBB or R,G,B)");
        } catch (NumberFormatException e) {
            throw new StructuraException("Invalid color format: " + s, e);
        }
    }
}