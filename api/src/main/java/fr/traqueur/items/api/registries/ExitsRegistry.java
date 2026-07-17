package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.exit.PipelineExit;
import fr.traqueur.items.api.models.Folder;

/**
 * Registry for managing PipelineExit instances.
 * <p>
 * Mirrors {@link EffectsRegistry}/{@link EntriesRegistry}, but for {@link PipelineExit} —
 * loaded from its own folder of yml files, referenced by id from pipeline effect settings.
 */
public interface ExitsRegistry extends Registry<String, PipelineExit> {

    /**
     * Loads exits from the configured folder structure.
     */
    void loadFromFolder();

    /**
     * Gets the root folder of the loaded folder structure, used for GUI navigation.
     *
     * @return the root Folder object
     */
    Folder<PipelineExit> getRootFolder();
}
