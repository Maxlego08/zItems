package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneXPBoostConfiguration extends RuneConfiguration {

    private final double xpBoost;

    public RuneXPBoostConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.xpBoost = configuration.getDouble("xp-boost", 1);
    }

    public double getXpBoost() {
        return xpBoost;
    }
}
