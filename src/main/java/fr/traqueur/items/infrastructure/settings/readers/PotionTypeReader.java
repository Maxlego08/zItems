package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.potion.PotionType;

public class PotionTypeReader implements Reader<PotionType> {
    @Override
    public PotionType read(String s) throws StructuraException {
        try {
            return PotionType.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StructuraException("Unknown potion type: " + s);
        }
    }
}