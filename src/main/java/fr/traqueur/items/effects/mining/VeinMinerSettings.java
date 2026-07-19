package fr.traqueur.items.effects.mining;

import fr.traqueur.items.api.settings.MaterialFilterSettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public record VeinMinerSettings(
        @Options(optional = true) List<Material> materials,
        @Options(optional = true) List<Tag<Material>> tags,
        @Options(optional = true) @DefaultBool(false) boolean blacklisted,
        int blockLimit,
        @Options(optional = true) @DefaultInt(-1) int damage,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements MaterialFilterSettings {

    public VeinMinerSettings {
        if (materials == null && tags == null) {
            throw new IllegalArgumentException("Either materials or tags must be provided");
        }
    }

}