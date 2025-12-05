package fr.traqueur.items.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.meta.trim.TrimMaterial;

public class TrimMaterialReader implements Reader<TrimMaterial> {
    @Override
    public TrimMaterial read(String s) throws StructuraException {
        return Registry.TRIM_MATERIAL.getOrThrow(NamespacedKey.minecraft(s));
    }
}