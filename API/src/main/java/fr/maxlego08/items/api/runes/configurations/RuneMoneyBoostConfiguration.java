package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneMoneyBoostConfiguration extends RuneConfiguration {

    private final double moneyBoost;

    public RuneMoneyBoostConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        this.moneyBoost = configuration.getDouble("money-boost", 1);
    }

    public double getMoneyBoost() {
        return moneyBoost;
    }
}
