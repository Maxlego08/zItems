package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.effects.Effect;
import fr.traqueur.items.api.models.Folder;

/**
 * Registry for managing Effect instances.
 */
public interface EffectsRegistry extends Registry<String, Effect> {

    /**
     * Loads effects from the configured folder structure.
     */
    void loadFromFolder();

    /**
     * Gets the root folder of the loaded folder structure, used for GUI navigation.
     *
     * @return the root Folder object
     */
    Folder<Effect> getRootFolder();
}
