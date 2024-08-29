package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.utils.TagRegistry;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class RuneConfiguration {

    protected final ItemPlugin plugin;
    protected final YamlConfiguration configuration;
    protected final String runeName;
    protected List<String> incompatibleRunes = new ArrayList<>();
    protected List<Material> materials = new ArrayList<>();
    protected List<Tag<Material>> tags = new ArrayList<>();
    protected boolean eventBlockBreakEvent = false;

    public RuneConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.runeName = runeName;
    }

    protected boolean loadEventBlockBreakEvent(String path) {
        return configuration.getBoolean(path);
    }

    protected List<Material> loadMaterials(String path) {
        return configuration.getStringList(path).stream().filter(Objects::nonNull).map(String::toUpperCase).map(value -> {
            try {
                return Material.valueOf(value);
            } catch (Exception exception) {
                plugin.getLogger().severe("Impossible to find the material " + value + " from rune " + runeName + ", Error: " + exception.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    protected List<Tag<Material>> loadTags(String path) {
        return configuration.getStringList(path).stream().filter(Objects::nonNull).map(String::toUpperCase).map(value -> {
            try {
                return TagRegistry.getTag(value);
            } catch (Exception exception) {
                plugin.getLogger().severe("Impossible to find the material tag " + value + " from rune " + runeName + ", Error: " + exception.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    public boolean contains(Material material) {
        if (this.materials.isEmpty() && this.tags.isEmpty()) {
            return true;
        }
        return this.materials.contains(material) || this.tags.stream().anyMatch(tag -> tag.isTagged(material));
    }

    public ItemPlugin getPlugin() {
        return plugin;
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }

    public String getRuneName() {
        return runeName;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public List<Tag<Material>> getTags() {
        return tags;
    }

    public List<String> getIncompatibleRunes() {
        return incompatibleRunes;
    }

    public boolean isEventBlockBreakEvent() {
        return eventBlockBreakEvent;
    }
}
