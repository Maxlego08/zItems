package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneVeinMiningConfiguration extends RuneConfiguration {

    private final int blockLimit;

    public RuneVeinMiningConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);
        int blockLimit = configuration.getInt("vein-mining.block-limit");

        if(configuration.contains("vein-mining.allowed-materials") && configuration.contains("vein-mining.blacklisted-materials")) {
            throw new IllegalArgumentException("You can't have both 'vein-mining.allowed-materials' and 'vein-mining.blacklisted-materials' in the same configuration file.");
        }

        if (configuration.contains("vein-mining.allowed-tags") && configuration.contains("vein-mining.blacklisted-tags")) {
            throw new IllegalArgumentException("You can't have both 'vein-mining.allowed-tags' and 'vein-mining.blacklisted-tags' in the same configuration file.");
        }

        if (configuration.contains("vein-mining.allowed-materials") && configuration.contains("vein-mining.blacklisted-tags")) {
            throw new IllegalArgumentException("You can't have both 'vein-mining.allowed-materials' and 'vein-mining.blacklisted-tags' in the same configuration file.");
        }

        if (configuration.contains("vein-mining.blacklisted-materials") && configuration.contains("vein-mining.allowed-tags")) {
            throw new IllegalArgumentException("You can't have both 'vein-mining.blacklisted-materials' and 'vein-mining.allowed-tags' in the same configuration file.");
        }

        if (configuration.contains("vein-mining.blacklisted-materials") && configuration.contains("vein-mining.blacklisted-tags")) {
            this.materials = this.loadMaterials("vein-mining.blacklisted-materials");
            this.tags = this.loadTags("vein-mining.blacklisted-tags");
            this.blacklisted = true;
        } else {
            this.materials = this.loadMaterials("vein-mining.allowed-materials");
            this.tags = this.loadTags("vein-mining.allowed-tags");
        }
        this.blockLimit = blockLimit;
    }

    public int blockLimit() {
        return blockLimit;
    }
}
