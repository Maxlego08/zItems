package fr.maxlego08.items.api.runes.configurations;

import fr.maxlego08.items.api.ItemPlugin;
import fr.maxlego08.items.api.configurations.commands.Action;
import fr.maxlego08.items.zcore.utils.plugins.Plugins;
import org.bukkit.configuration.file.YamlConfiguration;

public class RuneSellingConfiguration extends RuneConfiguration {

    private final Plugins plugin;
    private final Action action;
    private final double multiplier;
    private final boolean damage;

    public RuneSellingConfiguration(ItemPlugin plugin, YamlConfiguration configuration, String runeName) {
        super(plugin, configuration, runeName);

        String strPlugin = configuration.getString("plugin", "ALL");
        if(strPlugin.equalsIgnoreCase("ALL")) {
            this.plugin = null;
        } else {
            try {
                this.plugin = Plugins.valueOf(strPlugin);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Plugin " + strPlugin + " not found");
            }
        }
        this.multiplier = configuration.getDouble("multiplier", 1);
        this.damage = configuration.getBoolean("damage", true);
        try {
            this.action = Action.valueOf(configuration.getString("action", "CLICK"));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Action " + configuration.getString("action") + " not found");
        }
    }

    public Action getAction() {
        return action;
    }

    public boolean isDamage() {
        return damage;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public Plugins getPlugins() {
        return plugin;
    }
}
