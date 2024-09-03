package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

public class EmptyConfiguration extends RuneConfiguration {

    public EmptyConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);
    }
}
