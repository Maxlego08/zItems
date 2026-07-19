package fr.traqueur.items.effects.enhancement;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public record FireAroundSettings(
        @Options(optional = true) @DefaultDouble(2.0) double radius,
        @Options(optional = true) @DefaultInt(16) int points,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements EffectSettings {
}
