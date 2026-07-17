package fr.traqueur.items.effects.settings;

import fr.traqueur.items.api.effects.EffectSettings;
import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.effects.entries.PipelineEntry;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.annotations.defaults.DefaultBool;
import fr.traqueur.structura.references.Reference;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

/**
 * There is no dedicated "exit" concept: steps run in declaration order, so the
 * final action on the collected drops (sell, or nothing) is just whichever
 * effect is placed last in {@code steps} — e.g. an existing AUTO_SELL effect.
 * <p>
 * {@code steps} resolves lazily via {@link Reference} rather than eagerly at parse
 * time: a pipeline can otherwise only reference effects loaded before its own file,
 * since effect files load in directory order, not dependency order. Resolving at
 * {@code Reference#element()} call time (i.e. when the pipeline actually dispatches)
 * sidesteps that entirely — by then every effect is guaranteed loaded. See
 * {@code ZItems#validatePipelineReferences} for the startup warm-up pass that keeps
 * a genuinely broken reference a load-time error instead of a silent runtime one.
 */
public record PipelineSettings(
        PipelineEntry entry,
        List<Reference<Effect>> steps,
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
