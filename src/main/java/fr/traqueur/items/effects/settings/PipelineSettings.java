package fr.traqueur.items.effects.settings;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.effects.entries.Entry;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

/**
 * There is no dedicated "exit" concept: steps run in declaration order, so the
 * final action on the collected drops (sell, or nothing) is just whichever
 * effect is placed last in {@code steps} — e.g. an existing AUTO_SELL effect.
 */
public record PipelineSettings(
        Entry entry,
        List<Effect> steps,
        @Options(optional = true) List<Material> applicableMaterials,
        @Options(optional = true) List<Tag<Material>> applicableTags,
        @Options(optional = true) @DefaultBool(false) boolean applicabilityBlacklisted
) implements EffectSettings {

    public PipelineSettings {
        if (steps == null || steps.isEmpty()) {
            throw new IllegalArgumentException("A PIPELINE effect must declare at least one step");
        }
    }
}
