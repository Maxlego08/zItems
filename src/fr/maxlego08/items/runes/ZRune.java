package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneType;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public class ZRune implements Rune {

    private final String name;
    private final String displayName;
    private final RuneType type;
    private final List<Material> materials;
    private final List<Tag<Material>> tags;
    private final RuneConfiguration configuration;

    public ZRune(String name, String displayName, RuneType type, List<Material> materials, List<Tag<Material>> tags, RuneConfiguration configuration) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.materials = materials;
        this.tags = tags;
        this.configuration = configuration;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public RuneType getType() {
        return this.type;
    }

    @Override
    public List<Material> getMaterials() {
        return materials;
    }

    @Override
    public List<Tag<Material>> getTags() {
        return tags;
    }

    @Override
    public boolean isAllowed(Material material) {
        return materials.contains(material) || tags.stream().anyMatch(tag -> tag.isTagged(material));
    }

    @Override
    public <T extends RuneConfiguration> T getConfiguration() {
        return (T) this.configuration;
    }
}
