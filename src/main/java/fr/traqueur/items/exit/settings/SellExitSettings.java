package fr.traqueur.items.exit.settings;

import fr.traqueur.items.api.exit.ExitSettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import org.bukkit.Material;

import java.util.List;

public record SellExitSettings(
        @Options(optional = true) List<Material> materials,
        @Options(optional = true) @DefaultDouble(1.0) double multiplier
) implements ExitSettings {
}
