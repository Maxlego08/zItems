package fr.traqueur.items.api.exit;

import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;

/**
 * Marker interface for the settings of a {@link PipelineExit}.
 * <p>
 * Mirrors {@link fr.traqueur.items.api.effects.EffectSettings}/{@link fr.traqueur.items.api.entries.EntrySettings}.
 */
@Polymorphic(inline = true)
public interface ExitSettings extends Loadable {
}
