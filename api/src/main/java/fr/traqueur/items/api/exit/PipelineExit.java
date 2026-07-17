package fr.traqueur.items.api.exit;

/**
 * Represents a named, reusable "what happens to the pipeline's drops" step
 * (e.g. sell them, drop them, do nothing).
 * <p>
 * Mirrors {@link fr.traqueur.items.api.effects.Effect}/{@link fr.traqueur.items.api.entries.Entry}:
 * loaded from its own folder, referenced by id from a pipeline's settings, resolved to a
 * concrete {@link ExitHandler} via its {@link #type()}.
 */
public interface PipelineExit {

    /**
     * Gets the unique identifier of this exit.
     *
     * @return the exit ID
     */
    String id();

    /**
     * Gets the type of this exit, used to resolve the matching {@link ExitHandler}.
     *
     * @return the exit type
     */
    String type();

    /**
     * Gets the settings associated with this exit.
     *
     * @return the exit settings
     */
    ExitSettings settings();

}
