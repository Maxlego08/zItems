package fr.traqueur.items.effects.economy;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultDouble;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public record AutoSellSettings(
        @Options(optional = true) @DefaultDouble(1.0) double multiplier,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements EffectSettings {
}