package fr.traqueur.items.api.entries;

/**
 * Represents a named, reusable trigger condition that gates a {@code PIPELINE} effect.
 * <p>
 * Mirrors {@link fr.traqueur.items.api.effects.Effect}: loaded from its own folder,
 * referenced by id from a pipeline's settings, resolved to a concrete {@link EntryHandler}
 * via its {@link #type()}.
 */
public interface Entry {

    /**
     * Gets the unique identifier of this entry.
     *
     * @return the entry ID
     */
    String id();

    /**
     * Gets the type of this entry, used to resolve the matching {@link EntryHandler}.
     *
     * @return the entry type
     */
    String type();

    /**
     * Gets the settings associated with this entry.
     *
     * @return the entry settings
     */
    EntrySettings settings();

}
