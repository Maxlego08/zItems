package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.runes.Rune;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class RuneConfiguration {

    public RuneConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {}

}
