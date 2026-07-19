package fr.traqueur.items.infrastructure.settings.readers;

import fr.traqueur.structura.exceptions.StructuraException;
import fr.traqueur.structura.readers.Reader;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

public class TagReader implements Reader<Tag<Material>> {
    @Override
    public Tag<Material> read(String s) throws StructuraException {
        // Parse namespace and key
        NamespacedKey key;
        if (s.contains(":")) {
            String[] parts = s.split(":", 2);
            key = new NamespacedKey(parts[0], parts[1].toLowerCase());
        } else {
            // Default to minecraft namespace
            key = NamespacedKey.minecraft(s.toLowerCase());
        }

        // Try blocks registry first
        Tag<Material> blockTag = Bukkit.getTag(Tag.REGISTRY_BLOCKS, key, Material.class);
        if (blockTag != null) {
            return blockTag;
        }

        // Try items registry
        Tag<Material> itemTag = Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
        if (itemTag != null) {
            return itemTag;
        }

        throw new StructuraException("Tag '" + s + "' not found in blocks or items registry (searched for: " + key + ")");
    }
}
