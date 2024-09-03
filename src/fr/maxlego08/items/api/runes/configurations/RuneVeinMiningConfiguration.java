package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

public class RuneVeinMiningConfiguration extends RuneConfiguration {

    private final int blockLimit;

    public RuneVeinMiningConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);
        int blockLimit = configuration.getInt("vein-mining.block-limit");
        this.materials = this.loadMaterials("vein-mining.allowed-materials");
        this.tags = this.loadTags("vein-mining.allowed-tags");

        this.blockLimit = blockLimit;
    }

    public int blockLimit() {
        return blockLimit;
    }
}
