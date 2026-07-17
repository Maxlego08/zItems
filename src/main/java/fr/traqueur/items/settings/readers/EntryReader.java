package fr.traqueur.items.settings.readers;

import fr.traqueur.items.api.effects.entries.Entry;
import fr.traqueur.items.api.registries.EntriesRegistry;
import fr.traqueur.items.api.registries.Registry;
import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;

public class EntryReader implements Reader<Entry> {
    @Override
    public Entry read(String s) throws StructuraException {
        Entry entry = Registry.get(EntriesRegistry.class).getById(s);
        if (entry == null) {
            throw new StructuraException("Entry " + s + " not found in EntriesRegistry");
        }
        return entry;
    }
}
