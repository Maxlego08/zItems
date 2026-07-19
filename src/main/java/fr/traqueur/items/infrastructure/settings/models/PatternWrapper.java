package fr.traqueur.items.infrastructure.settings.models;

import fr.traqueur.structura.api.Loadable;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;

public record PatternWrapper(PatternType type, DyeColor color) implements Loadable {

    public Pattern toPattern() {
            return new Pattern(color, type);
        }

}