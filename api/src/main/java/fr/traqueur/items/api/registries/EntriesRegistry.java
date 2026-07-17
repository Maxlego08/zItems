package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.entries.Entry;
import fr.traqueur.items.api.models.Folder;

/**
 * Registry for managing Entry instances.
 * <p>
 * Mirrors {@link EffectsRegistry}, but for {@link Entry} — loaded from its own
 * folder of yml files, referenced by id from pipeline effect settings.
 */
public interface EntriesRegistry extends Registry<String, Entry> {

    /**
     * Loads entries from the configured folder structure.
     */
    void loadFromFolder();

    /**
     * Gets the root folder of the loaded folder structure, used for GUI navigation.
     *
     * @return the root Folder object
     */
    Folder<Entry> getRootFolder();
}
