package fr.maxlego08.items.api.runes.configurations;

import org.bukkit.configuration.file.YamlConfiguration;

public record RuneMeltMiningConfiguration() implements RuneConfiguration {
    public static RuneConfiguration loadConfiguration(YamlConfiguration configuration) {
        return new RuneMeltMiningConfiguration();
    }
}
