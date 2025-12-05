package fr.traqueur.items.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;

public class PatternTypeReader implements Reader<PatternType> {
    @Override
    public PatternType read(String value) throws StructuraException {
        Registry<PatternType> registry = Registry.BANNER_PATTERN;
        return registry.getOrThrow(NamespacedKey.minecraft(value.toLowerCase()));
    }
}
