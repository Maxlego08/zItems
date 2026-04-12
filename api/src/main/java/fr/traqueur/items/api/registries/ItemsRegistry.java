package fr.traqueur.items.api.registries;

import fr.traqueur.items.api.items.Item;
import fr.traqueur.items.api.models.Folder;

/**
 * Registry for managing Item instances.
 */
public interface ItemsRegistry extends Registry<String, Item> {

    /**
     * Loads items from the configured folder structure.
     */
    void loadFromFolder();

    /**
     * Gets the root folder of the loaded folder structure, used for GUI navigation.
     *
     * @return the root Folder object
     */
    Folder<Item> getRootFolder();
}
