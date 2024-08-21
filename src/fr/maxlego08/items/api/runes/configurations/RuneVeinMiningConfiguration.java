package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public record RuneVeinMiningConfiguration(
        int blockLimit,
        List<Material> materials,
        List<Tag<Material>> tags
) implements RuneConfiguration {

    public static RuneConfiguration loadConfiguration(YamlConfiguration configuration) {

        int blockLimit = configuration.getInt("vein-mining.block-limit");
        List<Material> materials = configuration.getStringList("vein-mining.allowed-materials").stream().map(String::toUpperCase).map(Material::valueOf).toList();
        List<Tag<Material>> tags = configuration.getStringList("vein-mining.allowed-tags").stream().map(String::toUpperCase).map(TagRegistry::getTag).toList();

        return new RuneVeinMiningConfiguration(blockLimit, materials, tags);
    }


    public boolean contains(Material material) {
        return this.materials.contains(material) || this.tags.stream().anyMatch(tag -> tag.isTagged(material));
    }
}
