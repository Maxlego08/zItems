package fr.traqueur.items.effects.farming;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.effects.drops.DropLocation;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.annotations.defaults.DefaultInt;
import fr.traqueur.structura.annotations.validation.Min;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public record FarmingHoeSettings(
        @Min(1) int size,
        @Options(optional = true) @DefaultBool(true) boolean autoReplant,
        @Options(optional = true) DropLocation dropLocation,
        @Options(optional = true) @DefaultBool(false) boolean dropInInventory,
        @Options(optional = true) @DefaultBool(true) boolean harvest,
        @Options(optional = true) @DefaultBool(true) boolean plantSeeds,
        @Options(optional = true) List<Material> dropBlacklist,
        @Options(optional = true) List<Material> allowedCrops,
        @Options(optional = true) List<Material> allowedSeeds,
        @Options(optional = true) @DefaultInt(1) int harvestDamage,
        @Options(optional = true) @DefaultInt(1) int tillDamage,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements EffectSettings {

    /**
     * Gets the range from the center based on size
     */
    public int range() {
        return size / 2;
    }

    /**
     * Checks if the size is valid (must be odd)
     */
    public boolean isValidSize() {
        return size % 2 != 0;
    }
}