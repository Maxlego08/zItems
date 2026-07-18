package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.damage.DamageType;
import org.bukkit.tag.DamageTypeTags;

public class DamageTypeReader implements Reader<Tag<DamageType>> {
    @Override
    public Tag<DamageType> read(String value) throws StructuraException {
        return Bukkit.getTag(DamageTypeTags.REGISTRY_DAMAGE_TYPES, NamespacedKey.minecraft(value), DamageType.class);
    }
}
