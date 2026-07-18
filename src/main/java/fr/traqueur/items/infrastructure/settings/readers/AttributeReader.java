package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;

import java.util.NoSuchElementException;

public class AttributeReader implements Reader<Attribute> {
    @Override
    public Attribute read(String s) throws StructuraException {
        try {
            return Registry.ATTRIBUTE.getOrThrow(NamespacedKey.minecraft(s.toLowerCase()));
        } catch (NoSuchElementException e) {
            throw new StructuraException("Unknown attribute: " + s);
        }

    }
}
