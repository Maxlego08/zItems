package fr.traqueur.items.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;

public class SoundReader implements Reader<Sound> {
    @Override
    public Sound read(String value) throws StructuraException {
        return Registry.SOUNDS.getOrThrow(NamespacedKey.fromString(value.toLowerCase()));
    }
}
