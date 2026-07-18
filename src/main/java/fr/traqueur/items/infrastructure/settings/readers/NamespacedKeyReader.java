package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;

public class NamespacedKeyReader implements Reader<NamespacedKey> {
    @Override
    public NamespacedKey read(String value) throws StructuraException {
        NamespacedKey key = NamespacedKey.fromString(value.toLowerCase());
        if (key == null) {
            throw new StructuraException("Invalid NamespacedKey: " + value);
        }
        return key;
    }
}
