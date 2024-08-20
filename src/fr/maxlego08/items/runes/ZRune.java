package fr.maxlego08.items.runes;

import fr.maxlego08.items.api.runes.Rune;
import fr.maxlego08.items.api.runes.RuneType;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.List;

public class ZRune implements Rune {

    private final String name;
    private final String displayName;
    private final RuneType type;
    private final List<Material> materials;
    private final List<Tag<Material>> tags;

    public ZRune(String name, String displayName, RuneType type, List<Material> materials, List<Tag<Material>> tags) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.materials = materials;
        this.tags = tags;
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
}
