package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class RuneVeinMiningConfiguration extends RuneConfiguration {

    private final int blockLimit;
    private final List<Material> materials;
    private final List<Tag<Material>> tags;

    public RuneVeinMiningConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);
        int blockLimit = configuration.getInt("vein-mining.block-limit");
        List<Material> materials = configuration.getStringList("vein-mining.allowed-materials").stream().map(String::toUpperCase).map(Material::valueOf).toList();
        List<Tag<Material>> tags = configuration.getStringList("vein-mining.allowed-tags").stream().map(String::toUpperCase).map(TagRegistry::getTag).toList();

        this.blockLimit = blockLimit;
        this.materials = materials;
        this.tags = tags;
    }

    public boolean contains(Material material) {
        return this.materials.contains(material) || this.tags.stream().anyMatch(tag -> tag.isTagged(material));
    }

    public int blockLimit() {
        return blockLimit;
    }

    public List<Material> materials() {
        return materials;
    }

    public List<Tag<Material>> tags() {
        return tags;
    }
}
