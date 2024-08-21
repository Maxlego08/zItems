package fr.maxlego08.items.runes;

import fr.maxlego08.items.ItemsPlugin;
import fr.maxlego08.items.api.runes.RuneActivator;
import fr.maxlego08.items.api.runes.RuneType;
import fr.maxlego08.items.api.runes.configurations.RuneConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneFarmingHoeConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneMeltMiningConfiguration;
import fr.maxlego08.items.api.runes.configurations.RuneVeinMiningConfiguration;
import fr.maxlego08.items.runes.activators.FarmingHoe;
import fr.maxlego08.items.runes.activators.MeltMining;
import fr.maxlego08.items.runes.activators.VeinMiner;
import org.bukkit.configuration.file.YamlConfiguration;

import java.lang.reflect.InvocationTargetException;

public enum RuneTypes implements RuneType {

    VEIN_MINING(new VeinMiner(), RuneVeinMiningConfiguration.class),
    MELT_MINING(new MeltMining(), RuneMeltMiningConfiguration.class),
    FARMING_HOE(new FarmingHoe(), RuneFarmingHoeConfiguration.class),
    ;

    private final RuneActivator<?> activator;
    private final Class<? extends RuneConfiguration> configuration;

    RuneTypes(RuneActivator<?> activator, Class<? extends RuneConfiguration> configuration) {
        this.activator = activator;
        this.configuration = configuration;
    }


    @Override
    public String getName() {
        return name();
    }

    @Override
    public RuneActivator<?> getActivator() {
        return this.activator;
    }

    @Override
    public RuneConfiguration getConfiguration(ItemsPlugin plugin, YamlConfiguration configuration, String runeName) {
        try {
            return this.configuration.getConstructor(ItemsPlugin.class, YamlConfiguration.class, String.class).newInstance(plugin, configuration, runeName);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
