package fr.traqueur.items.effects.entries;

import fr.traqueur.items.api.effects.entries.EntrySettings;
import fr.traqueur.structura.annotations.Options;
import fr.traqueur.structura.api.Loadable;

/**
 * Inline entry declaration, nested directly inside a pipeline's own settings —
 * mirrors {@code ZEffect}'s {@code type}/{@code settings} shape, just anonymous
 * (no {@code id}, no separate file/registry): entries have no ecosystem of their
 * own (no smithing table application, no GUI listing, no standalone reuse) the
 * way {@code Effect} does, so there's nothing to gain from splitting them out —
 * most carry no settings at all.
 */
public record PipelineEntry(
        String type,
        @Options(inline = true) EntrySettings settings
) implements Loadable {
}
