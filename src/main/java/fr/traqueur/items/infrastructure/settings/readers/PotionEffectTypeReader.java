package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.potion.PotionEffectType;

import java.util.Locale;

public class PotionEffectTypeReader implements Reader<PotionEffectType> {
    @Override
    public PotionEffectType read(String s) throws StructuraException {
        return Registry.EFFECT.getOrThrow(NamespacedKey.minecraft(s.toLowerCase()));
    }
}