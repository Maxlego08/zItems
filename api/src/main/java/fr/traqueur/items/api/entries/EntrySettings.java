package fr.traqueur.items.api.entries;

import fr.traqueur.structura.annotations.Polymorphic;
import fr.traqueur.structura.api.Loadable;

/**
 * Marker interface for the settings of an {@link Entry}.
 * <p>
 * Mirrors {@link fr.traqueur.items.api.effects.EffectSettings}, but without the
 * item-applicability contract — an entry only ever needs to answer "does this
 * event/context match?", it never gets applied to an item on its own.
 */
@Polymorphic(inline = true)
public interface EntrySettings extends Loadable {
}
