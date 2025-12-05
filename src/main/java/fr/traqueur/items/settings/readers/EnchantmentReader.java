package fr.traqueur.items.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;

import java.util.NoSuchElementException;

public class EnchantmentReader implements Reader<Enchantment> {
    @Override
    public Enchantment read(String s) throws StructuraException {
        Registry<Enchantment> enchantmentRegistry = Registry.ENCHANTMENT;
        try {
            return enchantmentRegistry.getOrThrow(NamespacedKey.minecraft(s.toLowerCase()));
        } catch (NoSuchElementException e) {
            throw new StructuraException("Enchantment " + s + " not found");
        }
    }
}
