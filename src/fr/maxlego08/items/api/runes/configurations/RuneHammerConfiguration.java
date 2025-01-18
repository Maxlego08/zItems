package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneHammerConfiguration extends RuneConfiguration {

    private final int height;
    private final int width;
    private final int depth;
    private final int damage;
    private final boolean isMaxDamage;

    public RuneHammerConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.height = configuration.getInt("height");
        this.width = configuration.getInt("width");
        this.depth = configuration.getInt("depth");
        this.eventBlockBreakEvent = this.loadEventBlockBreakEvent("enable-block-break-event");
        String damage = configuration.getString("damage");
        this.isMaxDamage = damage != null && damage.equalsIgnoreCase("max");
        this.damage = configuration.getInt("damage", 0);

        if(configuration.contains("hammer.allowed-materials") && configuration.contains("hammer.blacklisted-materials")) {
            throw new IllegalArgumentException("You can't have both 'hammer.allowed-materials' and 'hammer.blacklisted-materials' in the same configuration file.");
        }

        if (configuration.contains("hammer.allowed-tags") && configuration.contains("hammer.blacklisted-tags")) {
            throw new IllegalArgumentException("You can't have both 'hammer.allowed-tags' and 'hammer.blacklisted-tags' in the same configuration file.");
        }

        if (configuration.contains("hammer.allowed-materials") && configuration.contains("hammer.blacklisted-tags")) {
            throw new IllegalArgumentException("You can't have both 'hammer.allowed-materials' and 'hammer.blacklisted-tags' in the same configuration file.");
        }

        if (configuration.contains("hammer.blacklisted-materials") && configuration.contains("hammer.allowed-tags")) {
            throw new IllegalArgumentException("You can't have both 'hammer.blacklisted-materials' and 'hammer.allowed-tags' in the same configuration file.");
        }

        if (configuration.contains("hammer.blacklisted-materials") && configuration.contains("hammer.blacklisted-tags")) {
            this.materials = this.loadMaterials("hammer.blacklisted-materials");
            this.tags = this.loadTags("hammer.blacklisted-tags");
            this.blacklisted = true;
        } else {
            this.materials = this.loadMaterials("hammer.allowed-materials");
            this.tags = this.loadTags("hammer.allowed-tags");
        }
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isMaxDamage() {
        return isMaxDamage;
    }
}
