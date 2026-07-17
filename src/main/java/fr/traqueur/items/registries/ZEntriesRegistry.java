package fr.traqueur.items.registries;

import fr.traqueur.items.ZItems;
import fr.traqueur.items.api.ItemsPlugin;
import fr.traqueur.items.api.Logger;
import fr.traqueur.items.api.effects.entries.Entry;
import fr.traqueur.items.api.registries.EntriesRegistry;
import fr.traqueur.items.effects.entries.ZEntry;
import fr.traqueur.structura.api.Structura;
import fr.traqueur.structura.exceptions.StructuraException;

import java.nio.file.Path;

public class ZEntriesRegistry extends FileBasedRegistry<String, Entry> implements EntriesRegistry {

    public ZEntriesRegistry(ItemsPlugin plugin) {
        super(plugin, ZItems.ENTRIES_FOLDER, "Entries Registry");
    }

    @Override
    protected Entry loadFile(Path file) {
        try {
            Entry entry = Structura.load(file, ZEntry.class);
            this.register(entry.id(), entry);
            Logger.debug("Loaded entry: " + entry.id() + " from file: " + file.getFileName());
            return entry;
        } catch (StructuraException e) {
            Logger.severe("Failed to load entry from file: " + file.getFileName(), e);
            return null;
        }
    }
}
